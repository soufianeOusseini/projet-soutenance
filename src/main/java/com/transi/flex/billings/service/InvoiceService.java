package com.transi.flex.billings.service;


import com.transi.flex.billings.dao.InvoiceDAO;
import com.transi.flex.billings.dto.CreateInvoiceDTO;
import com.transi.flex.billings.dto.InvoiceDTO;
import com.transi.flex.billings.dto.PayInvoiceDTO;
import com.transi.flex.billings.enums.InvoiceStatus;
import com.transi.flex.billings.mapper.InvoiceMapper;
import com.transi.flex.billings.model.Invoice;
import com.transi.flex.billings.model.Subscription;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceService {

    private final InvoiceDAO invoiceRepository;
    private final CompanyRepository companyRepository;
    private final InvoiceMapper mapper;

    public InvoiceDTO createInvoice(CreateInvoiceDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Invoice invoice = mapper.toModel(dto);
        invoice.setCompany(company);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setStatus(InvoiceStatus.PENDING);

        Invoice saved = invoiceRepository.save(invoice);
        return mapper.toDto(saved);
    }

    public void generateInvoiceForSubscription(Subscription subscription) {
        Invoice invoice = new Invoice();
        invoice.setCompany(subscription.getCompany());
        invoice.setSubscription(subscription);
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setAmount(subscription.getPlan().getPrice());
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30)); // 30 jours pour payer
        invoice.setStatus(InvoiceStatus.PENDING);

        invoiceRepository.save(invoice);
    }

    public InvoiceDTO payInvoice(Long invoiceId, PayInvoiceDTO dto) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.PENDING) {
            throw new RuntimeException("Invoice is not in PENDING status");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaymentDate(dto.getPaymentDate() != null ? dto.getPaymentDate() : LocalDate.now());
        invoice.setPaymentMethod(dto.getPaymentMethod());

        Invoice saved = invoiceRepository.save(invoice);
        return mapper.toDto(saved);
    }

    public InvoiceDTO cancelInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot cancel a paid invoice");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        Invoice saved = invoiceRepository.save(invoice);
        return mapper.toDto(saved);
    }

    public InvoiceDTO getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public InvoiceDTO getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public List<InvoiceDTO> getInvoicesByCompany(Long companyId) {
        return invoiceRepository.findByCompanyId(companyId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getPendingInvoicesByCompany(Long companyId) {
        return invoiceRepository.findByCompanyIdAndStatus(companyId, InvoiceStatus.PENDING)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(InvoiceStatus.PENDING, LocalDate.now())
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceDTO> getInvoicesBySubscription(Long subscriptionId) {
        return invoiceRepository.findBySubscriptionId(subscriptionId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", (int) (Math.random() * 10000));

        String invoiceNumber = prefix + "-" + datePart + "-" + randomPart;

        // Vérifier l'unicité
        while (invoiceRepository.findByInvoiceNumber(invoiceNumber).isPresent()) {
            randomPart = String.format("%04d", (int) (Math.random() * 10000));
            invoiceNumber = prefix + "-" + datePart + "-" + randomPart;
        }

        return invoiceNumber;
    }

    public List<InvoiceDTO> getAll(){
        return mapper.toDtos(invoiceRepository.findAll());
    }
}