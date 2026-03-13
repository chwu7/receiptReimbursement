package com.fraternity.reimbursement.controller

import com.fraternity.reimbursement.dto.*
import com.fraternity.reimbursement.model.Role
import com.fraternity.reimbursement.model.User
import com.fraternity.reimbursement.repository.UserRepository
import com.fraternity.reimbursement.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )

            val user = userRepository.findByEmail(request.email)
                ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(mapOf("error" to "Invalid credentials"))

            val token = jwtService.generateToken(user)
            ResponseEntity.ok(AuthResponse(token = token, user = toUserResponse(user)))
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid credentials"))
        }
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        if (userRepository.existsByEmail(request.email)) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Email already in use"))
        }

        val user = userRepository.save(
            User(
                username = request.username,
                email = request.email,
                passwordHash = passwordEncoder.encode(request.password)!!,
                role = Role.MEMBER
            )
        )

        val token = jwtService.generateToken(user)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AuthResponse(token = token, user = toUserResponse(user)))
    }

    @GetMapping("/me")
    fun me(): ResponseEntity<Any> {
        val auth = SecurityContextHolder.getContext().authentication
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Not authenticated"))

        val userId = auth.principal as? Long
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Not authenticated"))

        val user = userRepository.findById(userId).orElse(null)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "User not found"))

        return ResponseEntity.ok(toUserResponse(user))
    }

    private fun toUserResponse(user: User) = UserResponse(
        id = user.id!!,
        email = user.email,
        username = user.username,
        role = user.role.name
    )
}
