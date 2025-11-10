# 🚖 Uber-Lite

A simplified ride-hailing backend built with **Java**, **Spring Boot**, and **JPA/Hibernate**.  
This project is a learning exercise to design and implement core features of an Uber-like service:
user management, driver onboarding, ride matching, and trip tracking.

---

## ✨ Features
- **User & Driver Registration** – sign up, authenticate, and manage profiles
- **Ride Request & Matching** – riders request a trip, nearby drivers receive offers
- **Trip Lifecycle** – accept ride, start trip, complete trip
- **Pricing & Payments (Planned)** – base fare, distance/time pricing, mock payment gateway
- **Admin Dashboard (Planned)** – basic analytics and management tools

---

## 🏗️ Tech Stack
| Layer            | Technology                          |
|------------------|--------------------------------------|
| Language         | Java 17                              |
| Framework        | Spring Boot 3                        |
| Persistence      | Spring Data JPA + Hibernate          |
| Database         | PostgreSQL (local or Docker)         |
| Build Tool       | Maven                                |
| API Docs         | OpenAPI/Swagger                      |
| Testing          | JUnit 5, Mockito                     |

---

## ⚙️ Getting Started

### Prerequisites
- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL** running locally or in Docker

### Clone & Run
```bash
git clone https://github.com/sriharsha032001/uber-lite.git
cd uber-lite
mvn clean install
mvn spring-boot:run

### Design patterns used 

| Pattern                              | Where It’s Used                                                       | Purpose                                                              |
| ------------------------------------ | --------------------------------------------------------------------- | -------------------------------------------------------------------- |
| **State Pattern**                    | `RideStateFactory`, `RequestedState`, `AssignedState`, `StartedState` | Manage ride lifecycle transitions cleanly                            |
| **Strategy Pattern**                 | `PricingStrategy`, `DriverMatchingStrategy`                           | Swap algorithms for pricing & driver selection dynamically           |
| **Decorator Pattern**                | `SurgePricingDecorator`                                               | Add surge multiplier behavior on top of base pricing                 |
| **Observer Pattern**                 | `EventBus` + `AuditLogger`                                            | Decouple side effects (audit logging, notifications) from core logic |
| **Repository Pattern**               | Spring Data JPA                                                       | Abstract persistence logic                                           |
| **Retry Logic + Optimistic Locking** | `assignDriverOnce()`                                                  | Handle concurrent driver assignment safely                           |
| **Idempotency Pattern**              | `IdempotencyKey` table                                                | Prevent duplicate ride creation on POST retries                      |
| **Rate Limiter (Token Bucket)**      | `RateLimiterService`                                                  | Throttle frequent ride requests per user                             |

Reliability Features
Concern	Solution
Duplicate ride requests	DB-backed Idempotency key
Concurrent driver assignment	Optimistic locking + retry loop
API spam	Rate limiter (token bucket per rider)
Transactional consistency	@Transactional per service operation
Resilient event handling	EventBus + decoupled subscribers
