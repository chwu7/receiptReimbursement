package com.fraternity.reimbursement.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "reimbursements")
class Reimbursement(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "form_id", nullable = false, updatable = false)
    val formId: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    val userId: Long = 0,

    @Column(name = "full_name", nullable = false)
    val fullName: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: ReimbursementCategory = ReimbursementCategory.OTHER,

    @Column(columnDefinition = "TEXT")
    val message: String? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    val amount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "payment_method", nullable = false)
    val paymentMethod: String = "",

    @Column(name = "receipt_r2_key", nullable = false, length = 512)
    val receiptR2Key: String = "",

    @Column(name = "receipt_url", nullable = false)
    val receiptUrl: String = "",

    @Column(name = "receipt_key", nullable = false)
    val receiptKey: String = "",

    @Column(name = "receipt_establishment")
    val receiptEstablishment: String? = null,

    @Column(name = "receipt_date")
    val receiptDate: String? = null,

    @Column(name = "receipt_total")
    val receiptTotal: Double? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ReimbursementStatus = ReimbursementStatus.PENDING,

    @Column(name = "submitted_at", nullable = false, updatable = false)
    val submittedAt: Instant = Instant.now(),

    @Column(name = "reviewed_at")
    val reviewedAt: Instant? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)
