# SecureFlow API Gateway

## Overview

SecureFlow uses Spring Cloud Gateway as the single backend entry point for client requests.

The API Gateway runs on port `8090` and routes requests to the appropriate backend microservice.

```text
Client
   |
   v
API Gateway :8090
   |
   +---- /api/auth/** --------> Auth Service :8081
   |
   +---- /api/users/** -------> User Service :8082
   |
   +---- /api/projects/** ----> Project Service :8083
   |
   +---- /api/notifications/** -> Notification Service :8084