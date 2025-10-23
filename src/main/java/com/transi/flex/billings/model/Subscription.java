package com.transi.flex.billings.model;
import com.transi.flex.company.model.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_SUBSCRIPTION")
public class Subscription {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "ACTIVE")
    private Boolean active;

    @ManyToOne
    private Company company;

    @ManyToOne
    private SubscriptionPlan plan;

    @Column(name = "AUTO_RENEW")
    private Boolean autoRenew = false;

    @Column(name = "CANCELLED_AT")
    private LocalDate cancelledAt;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;
}
