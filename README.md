# 📅 MiniDoodle

A mini calendar and meeting scheduling service built with **Spring Boot** and **Java**, that enables users to manage their time slots, schedule meetings, and view their calendar availability.
> **Note:** Due to time constraints, this is the current implementation. See the [Future Enhancements](#future-enhancements) section for planned improvements and features that could be added.
## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Domain Model](#domain-model)
- [Project Structure](#project-structure)
- [Setup & Installation](#setup--installation)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Current Limitations](#current-limitations)
- [Future Enhancements](#future-enhancements)

---
## Overview

MiniDoodle is a RESTful API service that provides the following functionalities:

### 🚀 Features

- **User Management**: Create, read, update, and delete users
- **Time Slot Management**:
    - Create available time slots with configurable duration
    - Modify or delete existing time slots
    - Mark time slots as **busy** or **available**
- **Meeting Scheduling**:
    - Convert available time slots into meetings
    - Add meeting details (title, description, participants)
    - Manage meeting participants
    - Cancel meetings
- **Availability Queries**:
    - Query free or busy slots
    - Get aggregated availability view for a selected time frame

### Design Decisions

- **Calendar in Domain Only**: As per requirements, the Calendar entity exists only in the domain layer without a dedicated controller, service, or repository. It is automatically created when a user is created and serves as a container for time slots.
- **UTC Time**: All timestamps are stored and processed in UTC to avoid timezone-related issues.
- **Instant over LocalDateTime**: Using `java.time.Instant` for all timestamps ensures consistent UTC handling.


---

## Tech Stack

- **Java 21+**
- **Spring Boot 4.0.1**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **Docker & Docker Compose**
- **Lombok**
- **SpringDoc OpenAPI 2.8.5**

---

## Architecture
The application follows a layered architecture pattern:
```declarative
+-------------------------------------------------------------+
|                     CONTROLLER LAYER                         |
|          (REST endpoints, request/response handling)         |
+-------------------------------------------------------------+
                            |
                            v
+-------------------------------------------------------------+
|                      SERVICE LAYER                           |
|               (Business logic, validations)                  |
+-------------------------------------------------------------+
                            |
                            v
+-------------------------------------------------------------+
|                     REPOSITORY LAYER                         |
|                 (Data access, JPA queries)                   |
+-------------------------------------------------------------+
                            |
                            v
+-------------------------------------------------------------+
|                       DOMAIN LAYER                           |
|                    (Entities, enums)                         |
+-------------------------------------------------------------+
                            |
                            v
+-------------------------------------------------------------+
|                        DATABASE                              |
|                      (PostgreSQL)                            |
+-------------------------------------------------------------+
```

### Layer Responsibilities

#### Controller Layer
- Handles HTTP requests and responses
- Maps DTOs to/from domain objects
- Delegates business logic to services
- Returns appropriate HTTP status codes

#### Service Layer
- Contains business logic and validations
- Manages transactions
- Orchestrates operations across repositories
- Enforces business rules

#### Repository Layer
- Provides data access abstraction
- Custom queries using Spring Data JPA
- Database operations (CRUD)

#### Domain Layer
- Contains entity definitions
- Encapsulates domain logic within entities
- Defines relationships between entities
- Note: Calendar exists only in this layer as per requirements

---

## Domain Model

### Entity Relationship Diagram
```declarative
+---------+       +----------+       +-----------+       +---------+
|  USER   |-(1:1)-| CALENDAR |-(1:N)-| TIME_SLOT |-(1:1)-| MEETING |
+---------+       +----------+       +-----------+       +---------+
|                                                           |
|                        (1:N organizer)                    |
+-----------------------------------------------------------+
|                                                           |
|                       (N:M participants)                  |
+-----------------------------------------------------------+
```
### Entities

#### User
- Represents a user of the system
- Automatically creates a Calendar upon user creation
- Can be an organizer or participant in meetings

#### Calendar (Domain Only)
- Personal calendar for each user
- Contains all time slots belonging to the user
- No dedicated API endpoints - managed through User and TimeSlot operations

#### TimeSlot
- Represents a block of time in a user's calendar
- Can be AVAILABLE or BUSY
- Can be converted into a meeting

#### Meeting
- Created from an available time slot
- Has an organizer (owner of the time slot)
- Can have multiple participants
- Automatically marks the time slot as BUSY


### Entities Description

#### User
| Field      | Type    | Description                    |
|------------|---------|--------------------------------|
| id         | Long    | Primary key, auto-generated    |
| email      | String  | Unique email address           |
| firstName  | String  | User's first name              |
| lastName   | String  | User's last name               |
| createdAt  | Instant | Timestamp of creation (UTC)    |
| calendar   | Calendar| One-to-one relationship        |

#### Calendar (Domain Only - No API)
| Field      | Type          | Description                    |
|------------|---------------|--------------------------------|
| id         | Long          | Primary key, auto-generated    |
| user       | User          | Owner of the calendar          |
| timeSlots  | List<TimeSlot>| All time slots in calendar     |
| createdAt  | Instant       | Timestamp of creation (UTC)    |

#### TimeSlot
| Field      | Type           | Description                    |
|------------|----------------|--------------------------------|
| id         | Long           | Primary key, auto-generated    |
| calendar   | Calendar       | Parent calendar                |
| startTime  | Instant        | Start time (UTC)               |
| endTime    | Instant        | End time (UTC)                 |
| status     | TimeSlotStatus | AVAILABLE or BUSY              |
| meeting    | Meeting        | Associated meeting (if any)    |
| createdAt  | Instant        | Timestamp of creation (UTC)    |

#### Meeting
| Field        | Type       | Description                    |
|--------------|------------|--------------------------------|
| id           | Long       | Primary key, auto-generated    |
| title        | String     | Meeting title                  |
| description  | String     | Meeting description            |
| timeSlot     | TimeSlot   | Associated time slot           |
| organizer    | User       | Meeting organizer              |
| participants | Set<User>  | Meeting participants           |
| createdAt    | Instant    | Timestamp of creation (UTC)    |

### Enums

#### TimeSlotStatus
| Value     | Description                           |
|-----------|---------------------------------------|
| AVAILABLE | Slot is free and can be booked        |
| BUSY      | Slot is occupied (has meeting or blocked) |

---
## Project Structure
```declarative
minidoodle/
├── src/main/java/com/challenge/minidoodle/
│   ├── MinidoodleApplication.java          # Application entry point
│   ├── config/
│   │   ├── JacksonConfig.java              # JSON serialization config
│   │   └── OpenApiConfig.java              # Swagger/OpenAPI config
│   ├── controller/
│   │   ├── UserController.java             # User REST endpoints
│   │   ├── TimeSlotController.java         # Time slot REST endpoints
│   │   ├── MeetingController.java          # Meeting REST endpoints
│   │   └── AdminController.java            # Admin utilities (dev only)
│   ├── domain/
│   │   ├── User.java                       # User entity
│   │   ├── Calendar.java                   # Calendar entity (domain only)
│   │   ├── TimeSlot.java                   # Time slot entity
│   │   ├── TimeSlotStatus.java             # Status enum
│   │   └── Meeting.java                    # Meeting entity
│   ├── dto/
│   │   ├── UserRequest.java                # User creation/update DTO
│   │   ├── UserResponse.java               # User response DTO
│   │   ├── TimeSlotRequest.java            # Time slot creation DTO
│   │   ├── TimeSlotUpdateRequest.java      # Time slot update DTO
│   │   ├── TimeSlotResponse.java           # Time slot response DTO
│   │   ├── AvailabilityResponse.java       # Availability query response
│   │   ├── MeetingRequest.java             # Meeting creation DTO
│   │   ├── MeetingUpdateRequest.java       # Meeting update DTO
│   │   └── MeetingResponse.java            # Meeting response DTO
│   ├── exception/
│   │   └── GlobalExceptionHandler.java     # Centralized exception handling
│   ├── repository/
│   │   ├── UserRepository.java             # User data access
│   │   ├── TimeSlotRepository.java         # Time slot data access
│   │   └── MeetingRepository.java          # Meeting data access
│   └── service/
│       ├── UserService.java                # User business logic
│       ├── TimeSlotService.java            # Time slot business logic
│       └── MeetingService.java             # Meeting business logic
├── src/main/resources/
│   ├── application.properties              # Application configuration
│   └── application.yml                     # Alternative YAML config
├── docker-compose.yml                      # Docker services definition
├── Dockerfile                              # Application container definition
└── pom.xml                                 # Maven dependencies
```

### Directory Descriptions

| Directory | Purpose |
|-----------|---------|
| config/ | Configuration classes (Jackson, OpenAPI) |
| controller/ | REST API endpoints |
| domain/ | JPA entities and enums |
| dto/ | Data Transfer Objects for requests/responses |
| exception/ | Global exception handling |
| repository/ | Spring Data JPA repositories |
| service/ | Business logic layer |


---
## Setup & Installation

### Prerequisites

- Docker and Docker Compose installed
- Java 17+ (for local development)
- Maven 3.8+ (for local development)

### Running with Docker (Recommended)

1. Clone the repository
   ```bash
   git clone <repository-url>
   cd minidoodle
    ```
2. Start all services
   ```bash
   docker compose up --build
    ```
3. Access the application
   - API Base URL: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html --> access here for api testing
   - OpenAPI JSON: http://localhost:8080/v3/api-docs

4. Stop services
   ```bash
    docker compose down
    ```
5. Stop and remove data   
    ```bash
   docker compose down -v
    ```
---
