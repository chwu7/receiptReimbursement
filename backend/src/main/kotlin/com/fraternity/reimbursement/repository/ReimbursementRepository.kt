package com.fraternity.reimbursement.repository

import com.fraternity.reimbursement.model.Reimbursement
import com.fraternity.reimbursement.model.ReimbursementStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ReimbursementRepository : JpaRepository<Reimbursement, Long> {

    fun findAllByOrderByCreatedAtDesc(): List<Reimbursement>

    @Modifying
    @Query("UPDATE Reimbursement r SET r.status = :status, r.reviewedAt = CURRENT_TIMESTAMP WHERE r.id = :id")
    fun updateStatus(id: Long, status: ReimbursementStatus): Int
}
