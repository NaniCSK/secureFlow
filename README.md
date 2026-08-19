# SecureFlow

## Multi-Tenant Identity & Workflow Platform

SecureFlow is a full-stack enterprise application designed to demonstrate
modern microservices architecture, secure authentication, authorization,
event-driven communication, and cloud-ready deployment.

The project is being built as a portfolio application to demonstrate
production-oriented backend and frontend engineering practices.

---

## Architecture

SecureFlow consists of an Angular frontend, Spring Boot microservices,
an API Gateway, PostgreSQL databases, and an event-driven communication
layer.

```text
Angular Frontend
       |
       v
API Gateway
       |
       +------------------+------------------+
       |                  |                  |
       v                  v                  v
Auth Service        User Service       Project Service
       |                  |                  |
       v                  v                  v
   auth_db             user_db          project_db
       |
       +------------------ Events ----------------+
                                                 |
                                                 v
                                               Kafka
                                                 |
                                                 v
                                      Notification Service