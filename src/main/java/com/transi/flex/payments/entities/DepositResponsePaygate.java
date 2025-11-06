package com.transi.flex.payments.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "T_DEPOSIT_RESPONSE_PAYGATE")
public class DepositResponsePaygate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "TX_REFERENCE")
    private String tx_reference;

    @Column(name = "STATUS")
    private String status;

    @JoinColumn(name = "REQUEST_ID")
    @ManyToOne
    private DepositRequestPaygate request;

    @CreationTimestamp
    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}
