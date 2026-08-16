# 🎟️ PlayTix — Sports Event Ticketing Platform

> A full-stack ticketing platform for sports events with reservation, payment, real-time availability, caching, indexed search, and role-based backend operations.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis)](https://redis.io/)
[![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.7.0-005571?logo=elasticsearch)](https://www.elastic.co/elasticsearch)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📌 Overview

**PlayTix** is a full-stack sports event ticketing platform designed to manage the complete ticketing lifecycle:

- User registration and authentication
- Sports, teams, leagues, tournaments, stadiums, and matches
- Ticket discovery and search
- Ticket reservation and confirmation
- Payment processing
- Ticket cancellation
- User profiles
- Reports and support workflows
- Administrative operations
- Redis caching and reservation locks
- Elasticsearch-powered ticket search

The project uses **PostgreSQL as the source of truth**, while Redis and Elasticsearch provide supporting infrastructure for caching, temporary data, locking, and search.

---

## 🏗️ Architecture

```text
                    ┌──────────────────────────┐
                    │        Frontend          │
                    │ HTML + CSS + JavaScript  │
                    └────────────┬─────────────┘
                                 │
                              REST API
                                 │
                    ┌────────────▼─────────────┐
                    │      Spring Boot         │
                    │ Controllers / Services   │
                    │ Security / Validation    │
                    └───────┬─────────┬────────┘
                            │         │
                 ┌──────────▼───┐   ┌─▼────────────────┐
                 │ PostgreSQL   │   │ Redis             │
                 │ Source of    │   │ Cache / OTP /     │
                 │ Truth        │   │ Reservation Locks │
                 └──────────────┘   └───────────────────┘
                            │
                            │ Ticket Search
                            ▼
                    ┌───────────────────┐
                    │   Elasticsearch   │
                    │ Indexed Search    │
                    └───────────────────┘
```

---

## 🧰 Tech Stack

### Backend
- Java 21
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Validation
- Spring Security
- Spring JDBC
- Maven
- JWT / JJWT
- MapStruct
- Lombok

### Database & Infrastructure
- PostgreSQL
- Redis 7
- Elasticsearch 8.7.0
- Docker Compose
- Testcontainers

### Frontend
- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

---

## ✨ Main Features

### 🔐 Authentication
- User signup
- Login
- JWT-based authentication
- Role-aware frontend flows
- Backend validation

### 🏟️ Sports & Events
The platform models:

- Sports
- Teams
- Leagues
- Tournaments
- Stadiums
- Matches
- Ticket categories

### 🎫 Ticketing
- Ticket categories and pricing
- Ticket status management
- Seat information
- Barcode / QR-related ticket fields
- Ticket search
- Ticket cancellation

### ⏱️ Reservations
The reservation module supports:

- Creating reservations
- Confirming reservations
- Cancelling reservations
- Expiring reservations
- Active reservations
- Paid reservations
- Administrative reservation views

### 💳 Payments
Payment APIs support:

- Creating payments
- Processing payments
- User payment history
- Successful payments
- Pending payments
- Administrative payment views
- Amount aggregation
- Expired pending-payment cleanup

### 🧑‍💼 Support & Reports
The project contains dedicated frontend and backend components for:

- Reports
- Support operations
- Administrative ticket management
- Administrative reports
- Administrative profiles

---

## ⚡ Redis

Redis is used for more than simple caching.

### OTP Storage
Six-digit OTPs are stored temporarily with TTL.

### User Profile Cache
Uses a **Cache-Aside** strategy:

```text
Request
   ↓
Redis?
 ┌─┴─────────────┐
 │ Hit           │ Miss
 ↓               ↓
Return       PostgreSQL
                 ↓
               Redis
```

### Ticket Search Cache
Repeated ticket searches can be served from Redis.

### Reservation Locks
Redis `SETNX` / `setIfAbsent` with TTL is used to prevent concurrent reservation of the same ticket.

PostgreSQL remains the source of truth.

---

## 🔎 Elasticsearch

Elasticsearch is dedicated to indexed ticket search.

Searchable fields include:

- Ticket ID
- Match ID
- Category
- Sport
- City
- Teams
- Price
- Ticket status

Example:

```http
GET /api/v1/tickets/elastic-search?sport=football&city=tehran
```

There is also an administrative reindex operation:

```http
POST /api/v1/admin/elastic/reindex-tickets
```

### Data Strategy

```text
PostgreSQL
    │
    │ source of truth
    ▼
Ticket Data
    │
    └──────────────► Elasticsearch
                     indexed search
```

Elasticsearch is **not** treated as the primary database.

---

## 📁 Project Structure

```text
sports-event-ticketing-platform/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/playtix/sports_event_ticketing_platform/
│   │   │       ├── api/
│   │   │       ├── config/
│   │   │       ├── domain/
│   │   │       ├── elastic/
│   │   │       ├── mapper/
│   │   │       ├── repository/
│   │   │       ├── scheduler/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │
│   └── test/
│
├── frontend/
│   ├── assets/
│   ├── css/
│   ├── js/
│   ├── index.html
│   ├── login.html
│   ├── signup.html
│   ├── results.html
│   ├── reservations.html
│   ├── payment.html
│   ├── profile.html
│   ├── reports.html
│   ├── edit-ticket.html
│   └── admin-*.html
│
├── database/
│   └── database.sql
│
├── docs/
│   ├── ERD.pdf
│   ├── ERD.png
│   └── ERD.vpp
│
├── docker-compose.yml
├── ELASTICSEARCH_IMPLEMENTATION.md
├── REDIS_IMPLEMENTATION.md
├── pom.xml
└── README.md
```

---

## 🗄️ Database Model

The SQL schema contains entities for:

`users`, `supports`, `sports`, `teams`, `tournaments`, `leagues`, `stadiums`, `matches`, `ticket_categories`, `tickets`, `payments`, `reports`, `reservations`, and `ticket_cancellations`.

Foreign keys are used to preserve relationships between the major entities.

An ERD is included in:

```text
docs/ERD.pdf
docs/ERD.png
docs/ERD.vpp
```

---

## 🧪 Testing

The project includes a Java test structure and Testcontainers dependencies for infrastructure-related testing.

Elasticsearch integration documentation includes a test path using temporary Elasticsearch and Redis containers.

Run tests with:

```bash
./mvnw test
```

On Windows:

```powershell
.\mvnw.cmd test
```

---

## 🐳 Infrastructure

Redis and Elasticsearch are provided through Docker Compose.

Start infrastructure:

```bash
docker compose up -d
```

Services:

| Service | Port |
|---|---:|
| Redis | `6379` |
| Elasticsearch | `9200` |

---

## 🚀 Running the Project

### 1. Clone

```bash
git clone https://github.com/alihasanlia/sports-event-ticketing-platform.git
cd sports-event-ticketing-platform
```

### 2. Prepare PostgreSQL

Create the database and schema using:

```text
database/database.sql
```

Configure the application's database settings according to the local environment.

### 3. Start Redis + Elasticsearch

```bash
docker compose up -d
```

### 4. Run the Spring Boot backend

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### 5. Run the frontend

Serve the `frontend/` directory through a local web server and open the required HTML page.

---

## 🔌 API Areas

| Area | Controller |
|---|---|
| Authentication | `AuthController` |
| Matches | `MatchController` |
| Sports | `SportController` |
| Teams | `TeamController` |
| Leagues | `LeagueController` |
| Stadiums | `StadiumController` |
| Reservations | `ReservationController` |
| Payments | `PaymentController` |
| Cancellations | `CancellationController` |
| Reports | `ReportController` |
| Support | `SupportController` |
| Elasticsearch Admin | `ElasticAdminController` |

---

## 🌿 Git Branches

The repository contains separate development branches including:

```text
main
backend
frontend
phase-1
redis
elastic-search
```

The `main` branch contains the final integrated project.

---

## 📚 Documentation

Additional implementation notes:

- [`ELASTICSEARCH_IMPLEMENTATION.md`](ELASTICSEARCH_IMPLEMENTATION.md)
- [`REDIS_IMPLEMENTATION.md`](REDIS_IMPLEMENTATION.md)
- [`database/database.sql`](database/database.sql)
- [`docs/ERD.pdf`](docs/ERD.pdf)

---

## 📄 License

This project is licensed under the MIT License.
