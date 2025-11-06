package com.transi.flex.billings.controller;

import com.transi.flex.billings.dto.CreateSubscriptionDTO;
import com.transi.flex.billings.dto.RenewSubscriptionDTO;
import com.transi.flex.billings.dto.SubscriptionDTO;
import com.transi.flex.billings.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody CreateSubscriptionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.createSubscription(dto));
    }

    @PostMapping("/renew")
    public ResponseEntity<SubscriptionDTO> renewSubscription(@RequestBody RenewSubscriptionDTO dto) {
        return ResponseEntity.ok(subscriptionService.renewSubscription(dto));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long id) {
        subscriptionService.cancelSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{companyId}/active")
    public ResponseEntity<SubscriptionDTO> getActiveSubscription(@PathVariable Long companyId) {
        return ResponseEntity.ok(subscriptionService.getActiveSubscriptionByCompany(companyId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<SubscriptionDTO>> getSubscriptionsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionsByCompany(companyId));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<SubscriptionDTO>> getExpiringSubscriptions(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(subscriptionService.getExpiringSubscriptions(days));
    }

    @PostMapping("/process-expired")
    public ResponseEntity<Void> processExpiredSubscriptions() {
        subscriptionService.processExpiredSubscriptions();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/auto-renew")
    public ResponseEntity<SubscriptionDTO> updateAutoRenew(
            @PathVariable Long id,
            @RequestBody Boolean autoRenew
    ) {
        return ResponseEntity.ok(subscriptionService.updateAutoRenew(id, autoRenew));
    }
}