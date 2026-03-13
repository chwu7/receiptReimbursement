package com.fraternity.reimbursement.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val username: String = "",

    @Column(nullable = false, unique = true)
    val email: String = "",

    @Column(name = "password_hash", nullable = false)
    val passwordHash: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.MEMBER,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now()
)
