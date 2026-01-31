# Tic Tac Toe Interview Project

A multi-module Spring Boot application built with Maven for a Tic Tac Toe interview project.

## Description

This is a multi-module Maven-based Spring Boot application that provides a foundation for building a Tic Tac Toe game. The project is organized into modules following a microservices-oriented architecture:

- **Tic Toe Common Module** - Contains shared domain models and entities
- **Tic Toe Engine Service** - Game engine microservice that manages game state, validates moves, and exposes REST endpoints
- **Tic Toe Session Service** - Session microservice that manages sessions and simulates gameplay via the engine service
- **Web Module** - Optional REST layer for health checks and future integrations
- **Tic Toe UI Service** - Angular UI for visualizing the automated game flow
- **Tic Toe Eureka Server** - Service registry for discovery
- **Tic Toe API Gateway** - Single entry point for routing API traffic

### Technology Stack

- **Spring Boot 3.2.0** - Latest stable version of Spring Boot
- **Java 17** - Modern Java version with enhanced features
- **Maven** - Dependency management and build tool
- **RESTful API** - Ready for building REST endpoints
- **Health Check Endpoint** - Basic health monitoring endpoint

## Project Structure

```
tic_toe_interview/
├── pom.xml                                    # Parent POM (aggregator)
├── README.md                                  # Project documentation
├── tic-toe-common/                            # Tic Toe Common Module
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── com/example/tictactoe/core/
│                   └── model/
│                       ├── Game.java          # Domain models
│                       └── GameResult.java    # Result models
├── tic-toe-engine-service/                    # Tic Toe Engine Service (Microservice)
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── com/example/tictactoe/engine/
│                   ├── TicToeEngineServiceApplication.java # Spring Boot app
│                   ├── GameEngine.java       # Game logic and rules
│                   ├── api/
│                   │   ├── GameController.java  # REST endpoints
│                   │   ├── dto/
│                   │   └── exception/
│                   └── session/
│                       ├── GameSessionService.java
│                       └── impl/
│                           └── GameSessionServiceImpl.java
├── tic-toe-session-service/                   # Tic Toe Session Service (Microservice)
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/tictactoe/session/
│           │       ├── TicToeSessionServiceApplication.java
│           │       ├── api/
│           │       ├── client/
│           │       ├── model/
│           │       └── service/
│           └── resources/
│               └── application.properties
└── web/                                       # Web Module
    ├── pom.xml
    └── src/
        └── main/
            ├── java/
            │   └── com/example/tictactoe/
            │       ├── TicTacToeApplication.java  # Main application class
            │       └── controller/
            │           └── HealthController.java   # Health check
            └── resources/
                └── application.properties         # Application configuration
└── tic-toe-ui-service/                        # Angular UI Service
    ├── package.json
    ├── angular.json
    └── src/
        ├── index.html
        ├── main.ts
        ├── styles.css
        └── app/
            ├── app.component.ts
            ├── app.component.html
            └── app.component.css
└── tic-toe-eureka-server/                     # Eureka Registry
    ├── pom.xml
    └── src/
        └── main/
            ├── java/
            └── resources/
                └── application.properties
└── tic-toe-api-gateway/                       # API Gateway
    ├── pom.xml
    └── src/
        └── main/
            ├── java/
            └── resources/
                └── application.properties
```

## Module Dependencies

The modules follow a microservices-oriented architecture with clear separation of concerns:

- **tic-toe-engine-service** → depends on **tic-toe-common**
- **tic-toe-session-service** → depends on **tic-toe-common**
- **web** → standalone (health check only)
- **tic-toe-common** → no dependencies (pure domain models)
- **tic-toe-eureka-server** → standalone registry
- **tic-toe-api-gateway** → depends on Eureka for service discovery

### Architecture Overview

- **Tic Toe Common Module**: Shared domain models used across modules
- **Tic Toe Engine Service**: Handles game state, move validation, and win detection (stateful)
- **Tic Toe Session Service**: Manages session lifecycle and simulation by coordinating with engine service
- **Inter-Service Communication**: Engine and Session communicate via Feign clients
- **Web Module**: Optional health check endpoint and future integrations
- **Eureka Server**: Service discovery registry
- **API Gateway**: Routes `/engine/**` and `/session/**` to backend services

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+** or higher
- **IDE** (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Getting Started

### 1. Build All Modules

From the root directory, build all modules:

```bash
mvn clean install
```

This will compile all modules in the correct order (tic-toe-common → tic-toe-engine-service → tic-toe-session-service → tic-toe-eureka-server → tic-toe-api-gateway → web).

### 2. Run the Engine Service

Since the Spring Boot application is in the `tic-toe-engine-service` module, run it from that directory:

```bash
cd tic-toe-engine-service
mvn spring-boot:run
```

Or run the main class `TicToeEngineServiceApplication` directly from your IDE.

Alternatively, from the root directory:

```bash
mvn spring-boot:run -pl tic-toe-engine-service
```

### 3. Run the Session Service

```bash
cd tic-toe-session-service
mvn spring-boot:run
```

Or run the main class `TicToeSessionServiceApplication` directly from your IDE.

### 4. Run Eureka + Gateway

```bash
cd tic-toe-eureka-server
mvn spring-boot:run
```

```bash
cd tic-toe-api-gateway
mvn spring-boot:run
```

Eureka UI: `http://localhost:8761`
Gateway: `http://localhost:8082`

### 5. Verify the Application

Once the application is running, you can verify it by accessing:

- **Health Check**: http://localhost:8080/api/health (web module, optional)
- **Game State**: http://localhost:8080/games/{gameId}
- **Game Session Details**: http://localhost:8080/games/{gameId}/session
- **Session State**: http://localhost:8081/sessions/{sessionId}
- **Gateway Engine Route**: http://localhost:8082/engine/games/{gameId}
- **Gateway Session Route**: http://localhost:8082/session/sessions/{sessionId}

### 6. Run the UI Service

```bash
cd tic-toe-ui-service
npm install
npm start
```

The UI will be available at `http://localhost:4200`.
By default it calls the API Gateway at `http://localhost:8082`.

### Docker (All Services)

Build the JARs first:

```bash
mvn -N install
mvn -pl tic-toe-eureka-server,tic-toe-engine-service,tic-toe-session-service,tic-toe-api-gateway -am clean package
```

Then start everything with Docker Compose:

```bash
docker compose up --build
```

Services:
- Eureka: `http://localhost:8761`
- Gateway: `http://localhost:8082`
- Engine: `http://localhost:8080`
- Session: `http://localhost:8081`
- UI: `http://localhost:4200`

You should see a JSON response:
```json
{
  "status": "UP",
  "message": "Tic Tac Toe Application is running"
}
```

### Game API Endpoints (Engine Service)

- **POST** `/games/{gameId}/move`  
  Body:
  ```json
  { "player": "X", "row": 0, "column": 0 }
  ```
  Validates the move, updates the game state, and returns the current status.

- **GET** `/games/{gameId}`  
  Retrieves the current game state (board and status).

- **GET** `/games/{gameId}/stream`  
  Server-Sent Events stream of game updates (pushes updates after each move).

- **GET** `/games/{gameId}/session`  
  Retrieves session details from the session service via Feign.

### Session API Endpoints (Session Service)

- **POST** `/sessions`  
  Creates a new session and initializes the game in the engine service.

- **POST** `/sessions/{sessionId}/simulate`  
  Simulates a game by generating moves and sending them to the engine service.

- **GET** `/sessions/{sessionId}`  
  Retrieves session details, including game state and move history.

- **GET** `/sessions/{sessionId}/game`  
  Proxies the current game state from the engine service (validates Feign communication).

## Configuration

Application configuration can be added under:

- `tic-toe-engine-service/src/main/resources/application.properties`
- `tic-toe-session-service/src/main/resources/application.properties`

- **Server Port**: Default is 8080 (engine), 8081 (session)
- **Feign Base URLs**: `session.base-url` (engine) and `engine.base-url` (session)
- **UI Base URL**: `ui.base-url` for CORS (default `http://localhost:4200`)
- **Eureka URL**: `http://localhost:8761/eureka`
- **Logging**: Configured to show INFO level logs, DEBUG for application packages

## Module Details

### Tic Toe Common Module

The `tic-toe-common` module contains:
- Shared domain models (Game, GameResult)
- Game status enums
- Value objects
- No framework dependencies (pure Java)

### Tic Toe Engine Service (Game Engine Microservice)

The `tic-toe-engine-service` module contains:
- Game state management (in-memory storage)
- Move validation logic
- Win condition detection
- Game status determination
- REST endpoints for move processing and state retrieval
- Depends on `tic-toe-common` module

### Tic Toe Session Service

The `tic-toe-session-service` module contains:
- Session lifecycle management
- Automated simulation of moves (rule-based)
- Coordination with engine service for move validation and state updates
- REST endpoints for session management
- Depends on `tic-toe-common` module

### Web Module

The `web` module contains:
- Health check endpoint (optional)
- Spring Boot application entry point
- Configuration files
- Contains all Spring Boot dependencies

## Dependencies

### Parent POM
- Manages common dependencies and versions
- Defines module structure
- Provides dependency management

### Tic Toe Common Module
- `spring-boot-starter-validation` - Bean validation support

### Tic Toe Engine Service
- `tic-toe-common` module dependency
- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-validation` - Bean validation support
- `spring-cloud-starter-openfeign` - Feign client for session service
- `spring-cloud-starter-netflix-eureka-client` - Service discovery client

### Tic Toe Session Service
- `tic-toe-common` module dependency
- `spring-boot-starter-web` - REST API support
- `spring-boot-starter-validation` - Bean validation support
- `spring-cloud-starter-openfeign` - Feign client for engine service
- `spring-cloud-starter-netflix-eureka-client` - Service discovery client

### Web Module
- `spring-boot-starter-web` - Web support (health check)
- `spring-boot-starter-test` - Testing support (JUnit, Mockito, etc.)
- `spring-boot-devtools` - Development tools for hot reload (optional)

### Eureka Server
- `spring-cloud-starter-netflix-eureka-server` - Service registry

### API Gateway
- `spring-cloud-starter-gateway` - Gateway routing
- `spring-cloud-starter-netflix-eureka-client` - Service discovery

## Building and Packaging

### Build All Modules

To build all modules and create JAR files:

```bash
mvn clean package
```

### Build Specific Module

To build only a specific module:

```bash
mvn clean install -pl tic-toe-common
mvn clean install -pl tic-toe-engine-service
mvn clean install -pl tic-toe-session-service
mvn clean install -pl tic-toe-eureka-server
mvn clean install -pl tic-toe-api-gateway
mvn clean install -pl web
```

### Create Executable JAR

The executable JAR will be created in the `tic-toe-session-service/target` directory:

```bash
java -jar tic-toe-session-service/target/tic-toe-session-service-1.0.0-SNAPSHOT.jar
```

## Architecture Benefits

This microservices-oriented architecture provides:

1. **Separation of Concerns**: Core models are isolated from the engine service logic
2. **Testability**: Engine service can be tested independently with in-memory state
3. **Scalability**: Engine service can be scaled as a dedicated microservice
4. **Maintainability**: Game rules and state management live in one cohesive service
5. **Reusability**: Core models are shared cleanly across modules

## Next Steps

To enhance the application, you might want to add:

1. **Tic Toe Engine Service**:
   - Additional game rules and variations
   - AI player logic
   - Game difficulty levels
   - Database persistence (replace in-memory storage)
   - Session expiration and cleanup
   - Game history tracking

2. **Tic Toe Session Service**:
   - More sophisticated simulation strategies
   - Replay functionality
   - Metrics and analytics for sessions

3. **Web Module**:
   - Additional REST endpoints
   - WebSocket support for real-time gameplay
   - API documentation (Swagger/OpenAPI)
   - Rate limiting and security

4. **Additional Features**:
   - Unit and integration tests for each module
   - Docker support for microservices
   - Service discovery and load balancing
   - Distributed tracing
   - Monitoring and metrics

## License

This project is created for interview purposes.
