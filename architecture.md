# Fraternity Reimbursement App — Architecture Reference

## Overview
A reimbursement submission and approval app for a fraternity. Members submit receipts with a description and amount. A vision AI model (Claude API) scans and validates the receipt against the claimed amount. Admins/treasurer can review, approve, or reject requests via a dashboard. Discord webhook notifications are sent to the treasurer on new submissions.

## Tech Stack
- **Backend:** Spring Boot + Kotlin
- **Database:** PostgreSQL
- **Image Storage:** AWS S3 (store receipt images, serve via presigned URLs)
- **Receipt Validation:** Claude API (multimodal vision — send receipt image, extract amount, compare against claimed amount)
- **Notifications:** Discord webhook (POST to webhook URL with embed containing amount, user, description, and presigned receipt image URL)
- **Auth:** Spring Security + JWT (stateless, token-based)
- **API Docs:** springdoc-openapi (Swagger UI at /swagger-ui.html)
- **Build System:** Gradle - Kotlin
- **JVM:** Java 21
- **Packaging:** Jar

## Dependencies
### From Spring Initializr:
- Spring Web
- Spring Data JPA
- PostgreSQL Driver
- Validation
- Spring Security
- Spring Boot DevTools
- Spring Boot Actuator

### Manually added to build.gradle.kts:
- `org.springdoc:springdoc-openapi-starter-webmvc-ui` (Swagger UI)
- `software.amazon.awssdk:s3` (AWS S3 SDK)
- `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson` (JWT auth)
- `com.anthropic:anthropic-java` (Claude vision API for receipt validation)

## Folder Structure

```
src/
├── main/
│   ├── kotlin/com/fraternity/reimbursement/
│   │   │
│   │   ├── ReimbursementApplication.kt          # Main entry point (@SpringBootApplication)
│   │   │
│   │   ├── config/
│   │   │   ├── SecurityConfig.kt                # Spring Security filter chain, JWT filter registration, role-based route protection
│   │   │   ├── S3Config.kt                      # S3Client bean configuration
│   │   │   └── WebConfig.kt                     # CORS config (for frontend), any other web-level config
│   │   │
│   │   ├── controller/
│   │   │   ├── AuthController.kt                # POST /api/auth/register, POST /api/auth/login → returns JWT
│   │   │   ├── ReimbursementController.kt       # POST /api/reimbursements (submit with file upload), GET /api/reimbursements (user's own)
│   │   │   └── AdminController.kt               # GET /api/admin/reimbursements (all requests), PATCH /api/admin/reimbursements/{id}/approve, PATCH .../reject — @PreAuthorize("hasRole('ADMIN')")
│   │   │
│   │   ├── service/
│   │   │   ├── AuthService.kt                   # User registration, login, password hashing, JWT generation
│   │   │   ├── ReimbursementService.kt          # Core business logic: save request, call S3, call vision API, call Discord, determine auto-approve vs pending
│   │   │   ├── S3Service.kt                     # Upload file to S3, generate presigned URLs for admin viewing
│   │   │   ├── ReceiptVisionService.kt          # Call Claude API with receipt image, parse structured response (extracted amount, vendor, date), compare against claimed amount
│   │   │   └── DiscordNotificationService.kt    # POST to Discord webhook URL with embed (amount, user, description, presigned receipt image URL)
│   │   │
│   │   ├── repository/
│   │   │   ├── UserRepository.kt                # JpaRepository<UserEntity, Long>, findByUsername(), findByEmail()
│   │   │   └── ReimbursementRepository.kt       # JpaRepository<ReimbursementEntity, Long>, findByUserId(), findByStatus()
│   │   │
│   │   ├── entity/
│   │   │   ├── UserEntity.kt                    # @Entity: id, username, email, passwordHash, role (enum: MEMBER, ADMIN, TREASURER), createdAt
│   │   │   └── ReimbursementEntity.kt           # @Entity: id, userId (FK), amountClaimed, amountExtracted (from vision API), description, receiptS3Key, status (enum: PENDING, AUTO_APPROVED, APPROVED, REJECTED), validationNotes, submittedAt, reviewedAt
│   │   │
│   │   ├── dto/
│   │   │   ├── AuthRequest.kt                   # Login/register request body (username, password)
│   │   │   ├── AuthResponse.kt                  # JWT token response
│   │   │   ├── SubmitReimbursementRequest.kt    # amount, description (sent as JSON part of multipart request)
│   │   │   ├── ReimbursementResponse.kt         # Response DTO returned to client (id, amount, description, status, receiptUrl, submittedAt, etc.)
│   │   │   └── ReceiptValidationResult.kt       # Data class from vision API: extractedAmount, vendor, date, matches (boolean), confidence
│   │   │
│   │   ├── security/
│   │   │   ├── JwtAuthenticationFilter.kt       # OncePerRequestFilter — extracts JWT from Authorization header, validates, sets SecurityContext
│   │   │   └── JwtTokenProvider.kt              # Generate JWT, validate JWT, extract claims (userId, role)
│   │   │
│   │   └── enum/
│   │       ├── Role.kt                          # MEMBER, ADMIN, TREASURER
│   │       └── Status.kt                        # PENDING, AUTO_APPROVED, APPROVED, REJECTED
│   │
│   └── resources/
│       ├── application.yml                      # DB connection, S3 config, Discord webhook URL, JWT secret, all via env vars
│       └── db/migration/                        # (Optional) Flyway migrations if using Flyway instead of Hibernate ddl-auto
│           ├── V1__create_users_table.sql
│           └── V2__create_reimbursements_table.sql
│
└── test/
    └── kotlin/com/fraternity/reimbursement/
        ├── controller/
        │   └── ReimbursementControllerTest.kt   # MockMvc tests for endpoints
        └── service/
            └── ReimbursementServiceTest.kt      # Unit tests for business logic
```

## Design Decisions

1. **Layered architecture:** Controller → Service → Repository. Controllers never touch the database directly. Services contain all business logic. Repositories are Spring Data JPA interfaces (Spring auto-generates implementations from method names).

2. **Stateless JWT auth:** No server-side sessions. JWT contains userId and role. Spring Security filter extracts and validates JWT on every request. Roles (MEMBER, ADMIN, TREASURER) control endpoint access via @PreAuthorize.

3. **S3 for receipt images:** Receipt images are uploaded to S3 on submission. Only the S3 object key is stored in Postgres. Admin dashboard loads images via presigned URLs (temporary, secure links) so the backend never proxies image bytes.

4. **AI receipt validation:** On submission, the receipt image is sent to Claude's vision API. The model extracts the total amount, vendor, and date. If the extracted amount matches the claimed amount (within a tolerance), the request is auto-approved. Otherwise, it's marked PENDING for manual admin review.

5. **Discord notifications:** A simple HTTP POST to a Discord webhook URL on each new submission. The payload includes an embed with the submitter's name, claimed amount, description, and the presigned receipt image URL. No bot setup required — just a webhook.

6. **DTOs separate from entities:** Request/response DTOs are separate from JPA entities. Entities map to database tables. DTOs define the API contract. Conversion happens in the service layer.

7. **Enums for role and status:** Role (MEMBER, ADMIN, TREASURER) and Status (PENDING, AUTO_APPROVED, APPROVED, REJECTED) are Kotlin enums stored as strings in Postgres via @Enumerated(EnumType.STRING).

8. **Configuration via environment variables:** All secrets and environment-specific config (DB credentials, S3 bucket, Discord webhook URL, JWT secret) are referenced in application.yml via ${ENV_VAR} syntax. Never hardcoded.