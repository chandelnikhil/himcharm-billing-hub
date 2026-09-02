package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.himcharm.entities.Customer;
import org.himcharm.entities.Invoice;
import org.himcharm.entities.InvoiceItem;
import org.himcharm.entities.Product;
import org.himcharm.entities.Store;
import org.himcharm.entities.WhatsAppMessage;
import org.himcharm.enums.WhatsAppMessageStatus;
import org.himcharm.enums.WhatsAppMessageType;
import org.himcharm.enums.WhatsAppTemplateCategory;
import org.himcharm.enums.WhatsAppStatus;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.InvoiceRepository;
import org.himcharm.repositories.WhatsAppMessageRepository;
import org.himcharm.services.CustomerService;
import org.himcharm.services.InvoiceService;
import org.himcharm.services.ProductService;
import org.himcharm.services.StoreService;
import org.himcharm.whatsapp.WhatsAppClientException;
import org.himcharm.whatsapp.WhatsAppService;
import org.himcharm.whatsapp.dto.InvoiceMessageRequest;
import org.himcharm.whatsapp.dto.WhatsAppMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private static final double ZERO = 0.0;
    private static final int PAGE_SIZE = 30;
    private static final DateTimeFormatter INVOICE_DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyy");

    private final InvoiceRepository invoiceRepository;
    private final StoreService storeService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final WhatsAppService whatsAppService;
    private final WhatsAppMessageRepository whatsAppMessageRepository;

    @Override
    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        Store store = storeService.getStoreById(invoice.getStore().getId());
        Customer customer = customerService.getOrCreateCustomerByPhone(
                invoice.getCustomer().getPhone(),
                invoice.getCustomer().getName()
        );
        invoice.setStore(store);
        invoice.setCustomer(customer);
        invoice.setInvoiceDate(invoice.getInvoiceDate() == null ? LocalDateTime.now() : invoice.getInvoiceDate());
        invoice.setWhatsappStatus(WhatsAppStatus.NOT_SENT);
        invoice.setSubtotal(ZERO);
        invoice.setTotalAmount(ZERO);

        double subtotal = ZERO;
        double totalAfterItemDiscounts = ZERO;
        for (InvoiceItem item : invoice.getItems()) {
            Product product = findProduct(item.getProduct());
            String itemName = resolveItemName(item, product);
            double unitPrice = money(item.getUnitPrice());
            double discountPercentage = money(item.getDiscountPercentage());
            double grossAmount = money(unitPrice * item.getQuantity());
            if (discountPercentage < 0.0 || discountPercentage > 100.0) {
                throw new IllegalStateException("Item discount percentage must be between 0 and 100: " + itemName);
            }

            double discountValue = grossAmount * discountPercentage / 100.0;
            double lineTotal = money(grossAmount - discountValue);
            item.setInvoice(invoice);
            item.setProduct(product);
            item.setItemName(itemName);
            item.setUnitPrice(unitPrice);
            item.setDiscountPercentage(discountPercentage);
            item.setLineTotal(lineTotal);
            subtotal += grossAmount;
            totalAfterItemDiscounts += lineTotal;
        }

        invoice.setSubtotal(money(subtotal));
        invoice.setTotalAmount(money(totalAfterItemDiscounts));

        Invoice savedInvoice = invoiceRepository.save(invoice);
        savedInvoice.setInvoiceNumber(buildInvoiceNumber(savedInvoice));
        savedInvoice = invoiceRepository.save(savedInvoice);

        WhatsAppMessage message = WhatsAppMessage.builder()
                .customer(savedInvoice.getCustomer())
                .invoice(savedInvoice)
                .messageType(WhatsAppMessageType.INVOICE)
                .phoneNumber(savedInvoice.getCustomer().getPhone())
                .templateName(whatsAppService.getInvoiceTemplateName())
                .templateCategory(WhatsAppTemplateCategory.UTILITY)
                .templateLanguage(whatsAppService.getInvoiceTemplateLanguage())
                .build();

        try {
            String normalizedPhoneNumber = whatsAppService.normalizePhoneNumber(
                    savedInvoice.getCustomer().getPhone()
            );
            message.setPhoneNumber(normalizedPhoneNumber);

            WhatsAppMessageResponse response = whatsAppService.sendInvoiceMessage(new InvoiceMessageRequest(
                    savedInvoice.getCustomer().getName(),
                    savedInvoice.getInvoiceNumber(),
                    normalizedPhoneNumber,
                    savedInvoice.getTotalAmount()
            ));

            message.setWhatsAppMessageId(firstMessageId(response));
            message.setStatus(WhatsAppMessageStatus.SENT);
            message.setSentAt(LocalDateTime.now());
            savedInvoice.setWhatsappStatus(WhatsAppStatus.SENT);
        } catch (WhatsAppClientException exception) {
            markMessageFailed(message, exception.getErrorCode(), exception.getMessage());
            savedInvoice.setWhatsappStatus(WhatsAppStatus.FAILED);
            log.warn("WhatsApp failed for invoice {}: {}", savedInvoice.getInvoiceNumber(), exception.getMessage());
        } catch (RuntimeException exception) {
            markMessageFailed(message, exception.getClass().getSimpleName(), exception.getMessage());
            savedInvoice.setWhatsappStatus(WhatsAppStatus.FAILED);
            log.warn("Unable to send WhatsApp message for invoice {}: {}",
                    savedInvoice.getInvoiceNumber(), exception.getMessage());
        }

        whatsAppMessageRepository.save(message);
        return invoiceRepository.save(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Invoice> getInvoices(int page, LocalDate fromDate, LocalDate toDate, Long storeId) {
        if (page < 0) {
            throw new IllegalStateException("Page number cannot be negative");
        }
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalStateException("From date cannot be after to date");
        }

        LocalDateTime fromDateTime = fromDate == null ? null : fromDate.atStartOfDay();
        LocalDateTime toDateExclusive = toDate == null ? null : toDate.plusDays(1).atStartOfDay();
        PageRequest pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "invoiceDate")
        );
        return invoiceRepository.findAllByInvoiceDateRange(fromDateTime, toDateExclusive, storeId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with number: " + invoiceNumber
                ));
    }

    private Product findProduct(Product product) {
        if (product == null || product.getId() == null) {
            return null;
        }
        return productService.getProduct(product.getId());
    }

    private String resolveItemName(InvoiceItem item, Product product) {
        if (item.getItemName() != null) {
            return item.getItemName();
        }
        if (product != null) {
            return product.getName();
        }
        throw new IllegalStateException("Item name is required when product ID is not provided");
    }

    private double money(Double amount) {
        return amount == null ? ZERO : Math.round(amount * 100.0) / 100.0;
    }

    private String buildInvoiceNumber(Invoice invoice) {
        return "INV-" + invoice.getStore().getStoreCode()
                + "-" + invoice.getInvoiceDate().format(INVOICE_DATE_FORMAT)
                + "-" + invoice.getId();
    }

    private String firstMessageId(WhatsAppMessageResponse response) {
        if (response.messages() == null || response.messages().isEmpty()) {
            return null;
        }
        return response.messages().getFirst().id();
    }

    private void markMessageFailed(WhatsAppMessage message, String errorCode, String errorMessage) {
        message.setStatus(WhatsAppMessageStatus.FAILED);
        message.setErrorCode(errorCode == null ? "UNKNOWN" : errorCode);
        message.setErrorMessage(errorMessage == null ? "Unknown WhatsApp error" : errorMessage);
        message.setFailedAt(LocalDateTime.now());
    }

}
