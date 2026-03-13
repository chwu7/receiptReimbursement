package com.fraternity.reimbursement.service

import com.fraternity.reimbursement.config.JwtProperties
import com.fraternity.reimbursement.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(private val jwtProperties: JwtProperties) {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateToken(user: User): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.expirationMs)

        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("role", user.role.name)
            .claim("username", user.username)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean =
        try {
            parseAllClaims(token)
            true
        } catch (_: Exception) {
            false
        }

    fun getUserId(token: String): Long =
        parseAllClaims(token).subject.toLong()

    fun getEmail(token: String): String =
        parseAllClaims(token)["email", String::class.java]

    fun getRole(token: String): String =
        parseAllClaims(token)["role", String::class.java]

    private fun parseAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}
