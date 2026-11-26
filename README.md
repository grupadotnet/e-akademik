# E-Akademik

Dormitory management platform developed by **Grupa .NET** at Cracow University of Technology (*Politechnika Krakowska*). The monorepo contains a **Spring Boot REST API** backend and a **React Native (Expo)** mobile application for dormitory residents and administration staff.

## Key Features

- **Authentication & Authorization** — Role-based access control (`RESIDENT`, `STAFF`, `ADMIN`) secured with JWT (`Bearer` header and `HttpOnly` `jwt_token` cookie).
- **User & Profile Management** — Registration and profile management for dormitory residents (university, dormitory, room assignment) and administration staff.
- **Dormitory & Shared Facility Reservations** — Domain model and reservation engine for shared dormitory facilities such as laundry rooms (time slots, reservation lifecycle).
- **Interactive API Documentation** — Auto-generated OpenAPI 3.1 specification and Swagger UI.

## Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.5.7 (Web, Security, Data JPA), JWT (`jjwt` 0.12.6), SpringDoc OpenAPI 2.8, Maven |
| **Database** | PostgreSQL 16 (Docker / production), H2 (in-memory local dev & unit tests), Testcontainers 1.21 (E2E tests) |
| **Mobile** | React Native 0.88, Expo SDK 58, Expo Router, React 19, TypeScript 6, Jest & React Native Testing Library |
| **DevOps & CI** | Docker, Docker Compose, GitHub Actions (`ci-backend.yml`, `ci-mobile.yml`) |

## Repository Structure

```text
e-akademik/
├── .github/workflows/       # GitHub Actions CI pipelines (backend & mobile)
├── backend/E-akademik/      # Spring Boot 3.5 REST API (Java 21)
│   ├── src/main/java/
│   │   ├── org/commons/                 # Shared domain value objects (UserId, Name, RoomNumber, TimeSlot, Role)
│   │   └── pl/grupanet/eakademik/       # Application modules (authentication, register, user, dormitory, reservation, controllers)
│   ├── src/test/java/                   # Unit tests & Testcontainers PostgreSQL E2E tests
│   ├── Dockerfile                       # Multi-stage Docker build (Maven -> Eclipse Temurin 21 JRE)
│   ├── docker-compose.yml               # Backend + PostgreSQL 16 Compose stack
│   └── .env.example                     # Environment variables template
└── mobile/                  # Expo / React Native mobile app (TypeScript)
    ├── src/app/                         # Expo Router file-based routes
    └── src/screens/                     # UI screens & component tests
```

## Getting Started

### 1. Backend (`backend/E-akademik`)

#### Option A: Run with Docker Compose (Recommended)

**Prerequisites:** Docker and Docker Compose.

```bash
cd backend/E-akademik
cp .env.example .env
```

Generate a 256-bit Base64 secret (for example with `openssl rand -base64 32`) and set `JWT_SECRET` and `POSTGRES_PASSWORD` in `.env`, then build and start the stack:

```bash
docker compose up -d --build
```

- **Backend API:** `http://localhost:8088`
- **Swagger UI:** `http://localhost:8088/swagger-ui/index.html`
- **Stop containers:** `docker compose down` (append `-v` to also remove the `postgres_data` volume)

#### Option B: Run Locally with Maven

**Prerequisites:** JDK 21 (e.g., Eclipse Temurin).

```bash
cd backend/E-akademik
cp .env.example .env
# Set JWT_SECRET in .env
./mvnw spring-boot:run
```

When `SPRING_DATASOURCE_URL` is not set, Spring Boot automatically falls back to an in-memory H2 database for local development.

#### Environment Variables

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `SERVER_PORT` | Backend HTTP port | `8088` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (`default` enables Swagger; `prod` disables it) | `default` |
| `JWT_SECRET` | **Required.** Base64-encoded 256-bit secret key used to sign JWTs | *(generated secret)* |
| `JWT_EXPIRATION` | JWT validity duration in milliseconds | `86400000` (24h) |
| `POSTGRES_DB` | PostgreSQL database name (Docker Compose) | `eakademik` |
| `POSTGRES_USER` | PostgreSQL username (Docker Compose) | `eakademik` |
| `POSTGRES_PASSWORD` | **Required (Docker).** PostgreSQL password | *(strong password)* |
| `POSTGRES_PORT` | Host port mapped to container port `5432` | `5432` |

---

### 2. Mobile Application (`mobile`)

**Prerequisites:** Node.js 22+ and npm (plus Expo Go on a physical device, Android Emulator, or iOS Simulator).

```bash
cd mobile
npm install
npm start
```

Platform shortcuts:

```bash
npm run android   # Launch on Android emulator / device
npm run ios       # Launch on iOS simulator / device (macOS)
npm run web       # Launch in web browser
```

## API Overview

All REST endpoints are versioned under `/api/v1`:

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Public | Authenticate user; returns JWT in JSON body and `HttpOnly` `jwt_token` cookie |
| `POST` | `/api/v1/auth/logout` | Public | Clear the `jwt_token` session cookie |
| `POST` | `/api/v1/auth/register/resident` | `STAFF` | Register a new dormitory resident account |
| `POST` | `/api/v1/auth/register/staff` | `ADMIN` | Register a new dormitory staff account |
| `GET` | `/api/v1/user/resident/{id}` | Authenticated | Get resident profile by UUID |
| `GET` | `/api/v1/user/residents?ids=...` | Authenticated | Get multiple resident profiles by UUIDs |
| `PUT` | `/api/v1/user/resident/{id}` | `STAFF`, `ADMIN` | Update resident profile and room assignment |
| `GET` | `/api/v1/user/staff/{id}` | Authenticated | Get staff profile by UUID |
| `GET` | `/api/v1/user/staff?ids=...` | Authenticated | Get multiple staff profiles by UUIDs |
| `PUT` | `/api/v1/user/staff/{id}` | `ADMIN` | Update staff profile |

Interactive documentation (when `SPRING_PROFILES_ACTIVE` is not `prod`):
- **Swagger UI:** `http://localhost:8088/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8088/v3/api-docs`

## Running Tests

### Backend

```bash
cd backend/E-akademik

# Full test suite (unit + PostgreSQL Testcontainers E2E — requires Docker running)
./mvnw test

# Unit tests only (no Docker required)
./mvnw test -Dtest='!*E2ETest'
```

### Mobile

```bash
cd mobile

# Run Jest test suite
npm test -- --watchAll=false

# TypeScript type-check
npx tsc --noEmit

# Lint
npm run lint
```
