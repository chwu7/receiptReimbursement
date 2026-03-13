package com.fraternity.reimbursement.config

import com.fraternity.reimbursement.model.Role
import com.fraternity.reimbursement.model.User
import com.fraternity.reimbursement.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataSeeder(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(DataSeeder::class.java)

    override fun run(args: ApplicationArguments) {
        val adminEmail = "charlestwu@berkeley.edu"

        if (!userRepository.existsByEmail(adminEmail)) {
            userRepository.save(
                User(
                    username = "charlestwu",
                    email = adminEmail,
                    passwordHash = passwordEncoder.encode("123456")!!,
                    role = Role.ADMIN
                )
            )
            log.info("Seeded default admin user: {}", adminEmail)
        }
    }
}
