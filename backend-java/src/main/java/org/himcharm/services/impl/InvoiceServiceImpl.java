package org.himcharm.services.impl;

import lombok.RequiredArgsConstructor;
import org.himcharm.entities.Customer;
import org.himcharm.entities.Invoice;
import org.himcharm.entities.InvoiceItem;
import org.himcharm.entities.Product;
import org.himcharm.entities.Store;
import org.himcharm.enums.WhatsAppStatus;
import org.himcharm.exceptions.ResourceNotFoundException;
import org.himcharm.repositories.InvoiceRepository;
import org.himcharm.services.CustomerService;
import org.himcharm.services.InvoiceService;
import org.himcharm.services.ProductService;
import org.himcharm.services.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final double ZERO = 0.0;
    private static final DateTimeFormatter INVOICE_DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyy");

    private final InvoiceRepository invoiceRepository;
    private final StoreService storeService;
    private final CustomerService customerService;
    private final ProductService productService;

    @Override
    @Transactional
    public Invoice createInvoice(Invoice invoice) {
        Store store = storeService.getStoreById(invoice.getStore().getId());
        Customer customer = customerService.getOrCreateCustomerByPhone(invoice.getCustomer().getPhone());
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
        return invoiceRepository.save(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
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

}
