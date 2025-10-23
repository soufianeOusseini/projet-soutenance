package com.transi.flex.billings.controller;

import com.transi.flex.billings.dto.CreateInvoiceDTO;
import com.transi.flex.billings.dto.InvoiceDTO;
import com.transi.flex.billings.dto.PayInvoiceDTO;
import com.transi.flex.billings.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody CreateInvoiceDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(dto));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<InvoiceDTO> payInvoice(@PathVariable Long id, @RequestBody PayInvoiceDTO dto) {
        return ResponseEntity.ok(invoiceService.payInvoice(id, dto));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<InvoiceDTO> cancelInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.cancelInvoice(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<InvoiceDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByCompany(companyId));
    }

    @GetMapping("/company/{companyId}/pending")
    public ResponseEntity<List<InvoiceDTO>> getPendingInvoicesByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(invoiceService.getPendingInvoicesByCompany(companyId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceDTO>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesBySubscription(@PathVariable Long subscriptionId) {
        return ResponseEntity.ok(invoiceService.getInvoicesBySubscription(subscriptionId));
    }

    @GetMapping("")
    public ResponseEntity<List<InvoiceDTO>> getAll(){
        return ResponseEntity.ok(invoiceService.getAll());
    }
}