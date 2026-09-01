package org.himcharm.services;

import org.himcharm.entities.Invoice;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface InvoiceService {

    Invoice createInvoice(Invoice invoice);

    Page<Invoice> getInvoices(int page, LocalDate fromDate, LocalDate toDate, Long storeId);

    Invoice getInvoiceById(Long id);

    Invoice getInvoiceByInvoiceNumber(String invoiceNumber);
}
