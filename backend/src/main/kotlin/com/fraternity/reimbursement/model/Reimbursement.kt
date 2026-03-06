package com.fraternity.reimbursement.model

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "reimbursements")
class Reimbursement(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val fullName: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    val message: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ReimbursementCategory = ReimbursementCategory.OTHER,

    @Column(nullable = false)
    val receiptUrl: String = "",

    @Column(nullable = false)
    val receiptKey: String = "",

    @Column
    val receiptEstablishment: String? = null,

    @Column
    val receiptDate: String? = null,

    @Column
    val receiptTotal: Double? = null,

    @Column
    val receiptCurrency: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ReimbursementStatus = ReimbursementStatus.PENDING,

    @Column(nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)
