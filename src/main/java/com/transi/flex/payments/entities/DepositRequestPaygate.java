package com.transi.flex.payments.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "T_DEPOSIT_REQUEST_PAYGATE")
public class DepositRequestPaygate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "PHONE_NUMBER")
    private String phone_number;

    @Column(name = "AMOUNT")
    private int amount;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IDENTIFIER")
    private String identifier;

    @Column(name = "NETWORK")
    private String network;

    @Column(name = "TX_REFERENCE")
    private String tx_reference;

    @Column(name = "STATUS")
    private int status;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private Instant updatedAt;

}
