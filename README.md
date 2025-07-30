# ETrain Microservice Platform

A comprehensive backend system for train e-ticketing built using Spring Boot microservices architecture. The system manages train schedules, seat bookings, coach management, route planning, and provides advanced features like Redis-based seat locking and automated ticket cleanup.

## Architecture Overview

This project implements a microservices architecture using Spring Boot 3.4.4 and Java 17, with PostgreSQL as the primary database and Redis for distributed locking and caching.

**Key Features:**
- Modular Spring Boot services for different business domains
- PostgreSQL database integration with JPA/Hibernate
- Redis-based distributed seat locking for concurrency control
- Automated scheduled jobs for ticket cleanup
- RESTful API endpoints with comprehensive CRUD operations
- Maven-based dependency management
- Transaction management with @Transactional support

## Microservices

| Service | Description | Features | Status |
|---------|-------------|----------|--------|
| **Train Service** | Manages trains, routes, stations, and schedules | Route planning, station management, train CRUD | ✅ Implemented |
| **Coach Seat Booking Service** | Handles seat reservations, bookings, and concurrency control | Redis locks, automated cleanup, payment processing | ✅ Implemented |

## Technology Stack

- **Backend Framework:** Spring Boot 3.4.4
- **Language:** Java 17
- **Database:** PostgreSQL
- **Cache & Locks:** Redis with Lettuce
- **ORM:** JPA/Hibernate
- **Build Tool:** Maven
- **Architecture:** Microservices
- **Cleanup Scheduling:** Spring @Scheduled
- **Transactions:** Spring @Transactional

## Core Features

### Train Management Service
- **Station Management**: CRUD operations for railway stations
- **Train Management**: Train registration, scheduling, and metadata
- **Route Planning**: 
  - Direct route finding between stations
  - Multi-hop route planning with continuous path detection
  - Route management with arrival/departure times
  - Advanced route filtering and sorting

### Coach & Seat Booking Service
- **Coach Management**: 
  - Multiple coach classes (AC_B, AC_S, SNIGDHA, F_BERTH, F_SEAT, F_CHAIR, S_CHAIR, SHOVAN, SHULOV, AC_CHAIR)
  - Dynamic seat generation (up to 26 rows, 4 seats per row)
  - Coach-specific pricing tiers

- **Advanced Seat Locking**:
  - Redis-based distributed locking with configurable timeout (default: 10 minutes)
  - Concurrent booking prevention
  - User-specific seat reservations
  - Automatic lock expiration and cleanup

- **Booking Management**:
  - Multi-passenger ticket creation
  - Passenger type-based pricing (ADULT: ₹300, CHILD: ₹100)
  - Seat status management (AVAILABLE/UNAVAILABLE)
  - Payment processing and ticket validation
  - Booking updates and cancellations

- **Automated Background Jobs**:
  - Scheduled ticket cleanup (every 1 minute)
  - Automatic expiration of unpaid tickets (10-minute timeout)
  - Seat unlocking and status restoration
  - Database cleanup for expired reservations

## Project Structure

```
ETrain_Microservice/
├── Train/
│   └── Train/                    # Train Management Service
│       ├── src/main/java/com/TrainMS/Train/
│       │   ├── controllers/      # REST Controllers (Train, Route, Station)
│       │   ├── models/          # JPA Entities (Train, Route, Station)
│       │   ├── repositories/    # Data Access Layer
│       │   └── services/        # Business Logic (Route planning, Train management)
│       └── pom.xml
├── Coach_Seat_Booking/
│   └── Coach_Seat_Booking/       # Booking Management Service
│       ├── src/main/java/com/Coach_Seat_BookingMS/Coach_Seat_Booking/
│       │   ├── config/          # Redis Configuration
│       │   ├── controllers/     # REST Controllers (Booking, Coach)
│       │   ├── dtos/           # Data Transfer Objects
│       │   ├── enums/          # Business Enums (CoachClass, SeatStatus, PassengerType)
│       │   ├── models/         # JPA Entities (Ticket, Seat, Coach, Passengers)
│       │   ├── repositories/   # Data Access Layer
│       │   ├── scheduledJobs/  # Background Jobs (TicketCleanupJob)
│       │   └── services/       # Business Logic (Booking, SeatLock, CoachSeat)
│       └── pom.xml
├── train_service_data.sql        # Database schema and sample data
└── README.md
```

## Database Setup

The project uses PostgreSQL as the primary database with sample data provided.

```sql
-- Create database
CREATE DATABASE train_service_db;

-- Import sample data
\i train_service_data.sql
```

### Sample Data Includes:
- 5 Railway stations (Station1-Station5)
- Pre-configured train routes
- Coach and seat configurations

## Redis Setup

Redis is required for distributed seat locking. Configure Redis connection in `application.properties`:

```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=1234
spring.redis.timeout=5000ms
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=0
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Redis 6.0+

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/NIHAJ-RARBA/ETrain_Microservice.git
   cd ETrain_Microservice
   ```

2. Set up PostgreSQL database:
   ```bash
   psql -U postgres -f train_service_data.sql
   ```

3. Start Redis server:
   ```bash
   redis-server
   ```

4. Configure database connections in `application.properties` for each service

5. Build and run each service:
   ```bash
   # Train Service (Default port: 8080)
   cd Train/Train
   mvn spring-boot:run
   
   # Coach Seat Booking Service (Configure different port if needed)
   cd Coach_Seat_Booking/Coach_Seat_Booking
   mvn spring-boot:run
   ```

## Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/train_service_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Redis Configuration
```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=your_redis_password
```

### Seat Lock Configuration
```properties
seat.lock.duration.seconds=600  # 10 minutes default
```

## API Endpoints

### Train Service
- **Trains**: GET, POST, PUT, DELETE `/api/trains`
- **Routes**: GET, POST, PUT, DELETE `/api/routes`
- **Stations**: GET, POST, PUT, DELETE `/api/stations`
- **Route Planning**: GET `/api/routes/search` (source to destination)

### Coach Seat Booking Service
- **Booking Management**:
  - `POST /booking/create-ticket` - Create new booking
  - `PUT /booking/update` - Update existing booking
  - `PUT /booking/paid` - Mark ticket as paid
  - `DELETE /booking/cancel` - Cancel booking
  - `GET /booking/all` - Get all tickets
  - `GET /booking/byUserId` - Get tickets by user
  - `GET /booking/byUserIdAndCoachId` - Get specific user-coach tickets

- **Coach Management**:
  - `GET /coach/all` - Get all coaches
  - `POST /coach/create` - Create new coach
  - `GET /coach/byClass` - Get coaches by class
  - `GET /coach/byRoute` - Get coaches by route

## Business Logic Features

### Advanced Route Planning
- **Direct Routes**: Find immediate connections between stations
- **Multi-hop Routes**: Plan complex journeys with transfers
- **Route Optimization**: Continuous path detection across train networks
- **Time-based Filtering**: Arrival/departure time considerations

### Concurrency Control
- **Redis Distributed Locks**: Prevent race conditions in seat booking
- **User-specific Reservations**: Seats locked per user with timeout
- **Automatic Cleanup**: Background jobs handle expired reservations
- **Transaction Management**: ACID compliance for booking operations

### Pricing System
- **Coach Class Pricing**: 10 different coach classes with base prices (₹700-₹1500)
- **Passenger Type Pricing**: Adult (₹300) and Child (₹100) supplements
- **Dynamic Pricing**: Automatic calculation based on selections

### Automated Operations
- **Ticket Expiration**: 10-minute payment window
- **Seat Unlocking**: Automatic release of expired reservations
- **Database Cleanup**: Orphaned record removal
- **Status Synchronization**: Real-time seat availability updates

## Testing

Run tests for each service:

```bash
cd [service-directory]
mvn test
```

## Development Status

This is an active development project with comprehensive booking and route management capabilities:

**Completed Features:**
- ✅ Full train and route management
- ✅ Advanced booking system with concurrency control
- ✅ Redis-based distributed locking
- ✅ Automated background job processing
- ✅ Multi-coach class support
- ✅ Dynamic pricing system
- ✅ Database transaction management
- ✅ RESTful API with comprehensive endpoints

**Architecture Highlights:**
- Microservices with clear separation of concerns
- Event-driven background processing
- Distributed system design patterns
- Production-ready error handling and logging

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
