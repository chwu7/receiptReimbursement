package com.fraternity.reimbursement.repository

import com.fraternity.reimbursement.model.Reimbursement
import org.springframework.data.jpa.repository.JpaRepository

interface ReimbursementRepository : JpaRepository<Reimbursement, Long>
