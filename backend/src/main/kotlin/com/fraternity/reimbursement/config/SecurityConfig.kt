package com.fraternity.reimbursement.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager {
        val provider = DaoAuthenticationProvider(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder)
        return ProviderManager(provider)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    // Public: auth endpoints
                    .requestMatchers("/api/auth/**").permitAll()
                    // Public: submission form metadata (categories)
                    .requestMatchers(HttpMethod.GET, "/api/reimbursements/form").permitAll()
                    // Authenticated members + admins: submitting a reimbursement
                    .requestMatchers(HttpMethod.POST, "/api/reimbursements").hasAnyRole("MEMBER", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/reimbursements/validate-receipt").hasAnyRole("MEMBER", "ADMIN")
                    // Public: static frontend files, actuator, swagger
                    .requestMatchers("/", "/index.html", "/**/*.css", "/**/*.js", "/**/*.ico").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    // Admin only: viewing all submissions + changing status
                    .requestMatchers(HttpMethod.GET, "/api/reimbursements").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/reimbursements/*/status").hasRole("ADMIN")
                    // Everything else requires authentication
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}
