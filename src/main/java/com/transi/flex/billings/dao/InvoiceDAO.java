package com.transi.flex.billings.dao;

import com.transi.flex.billings.enums.InvoiceStatus;
import com.transi.flex.billings.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InvoiceDAO extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCompanyId(Long companyId);

    List<Invoice> findByCompanyIdAndStatus(Long companyId, InvoiceStatus status);

    List<Invoice> findByStatus(InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.dueDate < :currentDate")
    List<Invoice> findOverdueInvoices(InvoiceStatus status, LocalDate currentDate);

    List<Invoice> findBySubscriptionId(Long subscriptionId);
}
