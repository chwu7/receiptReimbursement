package com.fraternity.reimbursement.repository

import com.fraternity.reimbursement.model.Reimbursement
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReimbursementRepository : JpaRepository<Reimbursement, UUID>
