# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Receipt Reimbursement application with a Spring Boot 4.0.3 backend written in Kotlin, using PostgreSQL for persistence.

## Build & Run Commands

All commands run from the `backend/` directory:

```bash
cd backend

./gradlew build          # Full build (compile + test)
./gradlew clean build    # Clean build
./gradlew bootRun        # Run the application
./gradlew test           # Run all tests (JUnit 5)
```

On Windows, use `gradlew.bat` instead of `./gradlew` if not in a Unix-like shell.

## Tech Stack & Dependencies

- **Kotlin** on JDK 21 with Spring Boot 4.0.3
- **Spring Web MVC** for REST APIs
- **Spring Data JPA** with PostgreSQL
- **Spring Security** for auth
- **Spring Boot Validation** for input validation
- **Spring Boot Actuator** for health/monitoring
- **Jackson Kotlin module** for JSON serialization
- **Gradle 9.3.1** (Kotlin DSL) as build system

## Architecture

- Standard Spring Boot MVC structure under `backend/src/main/kotlin/com/example/demo/`
- Entry point: `DemoApplication.kt` with `@SpringBootApplication`
- Kotlin all-open plugin configured for JPA entities (`@Entity`, `@MappedSuperclass`, `@Embeddable`)
- JSR305 strict null-safety enabled via compiler options
- Configuration in `backend/src/main/resources/application.properties`
- Environment variables in `.env` at project root
