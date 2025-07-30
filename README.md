# 🚆 ETrain Microservice Platform

A scalable, modular backend for an e-ticketing system for trains — built using microservices architecture and inspired by [railapp.railway.gov.bd](https://railapp.railway.gov.bd). Designed to manage train schedules, reservations, payments, and notifications with modern development best practices.

---

## 🧱 Architecture Overview

This project splits backend functionality across independently deployable **microservices**, promoting separation of concerns, scalability, and resilience.

**Key features include:**
- Modular services: Booking, Trains, Users, Authentication, Notifications, Payments
- Event-driven communication via message brokers (e.g., RabbitMQ/Kafka)
- API Gateway for unified access
- Docker-based deployment and orchestration

---

## 🧩 Microservices Breakdown

| Service             | Description                                         | Tech Stack     |
|---------------------|-----------------------------------------------------|----------------|
| **API Gateway**      | Entry point for all client requests (routing, auth) | Node.js / Express |
| **Auth Service**     | Handles user login, registration, and JWT tokens   | Node.js / JWT |
| **User Service**     | Manages passenger details and user profiles        | Node.js / MongoDB |
| **Train Service**    | Handles train data: schedules, availability        | Node.js / MongoDB |
| **Booking Service**  | Manages seat reservations and booking logic        | Node.js / MongoDB |
| **Payment Service**  | Integrates with payment gateways for transactions  | Node.js / Stripe |
| **Notification Service** | Sends SMS/Email notifications                | Node.js / SMTP |

---

## 📁 Project Structure

```
ETrain_Microservice/
├── api-gateway/
├── auth-service/
├── booking-service/
├── user-service/
├── train-service/
├── payment-service/
├── notification-service/
├── common/               # Shared resources (DTOs, events, utilities)
├── docker-compose.yml
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose
- Node.js v18+ or compatible runtime
- MongoDB instance (local or Docker)
- RabbitMQ or Kafka for event messaging

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/NIHAJ-RARBA/ETrain_Microservice.git
   cd ETrain_Microservice
   ```

2. Run all services:

   ```bash
   docker-compose up --build
   ```

3. Access API at:

   ```
   http://localhost:8000
   ```

---

## 🔐 Environment Variables

Each service has a `.env` file for configuration. Common variables include:

```env
PORT=3000
JWT_SECRET=your_jwt_secret
MONGO_URI=mongodb://mongo:27017/service_db
RABBITMQ_URL=amqp://rabbitmq
```

---

## 🧪 Testing

Run unit and integration tests (example for one service):

```bash
cd auth-service
npm install
npm test
```

---

## 🛰️ Communication Strategy

- **Synchronous**: API Gateway to services via REST
- **Asynchronous**: Event-driven interactions using RabbitMQ
- **Patterns**: Follows event sourcing, API gateway pattern, and service discovery

---

## 📖 API Documentation

Each service has Swagger/OpenAPI documentation hosted locally:

- Auth: `http://localhost:3001/docs`
- Booking: `http://localhost:3002/docs`
- Train: `http://localhost:3003/docs`
*(Ensure services are running to access)*

---

## 📌 Roadmap

- [ ] Add admin panel
- [ ] Implement distributed tracing
- [ ] Build reporting and analytics dashboard



## 📄 License

MIT License — see [LICENSE](LICENSE) file for details.

---

Issues & suggestions: [GitHub Issues](https://github.com/NIHAJ-RARBA/ETrain_Microservice/issues)
