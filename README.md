# Workify - Job Portal (Backend)

A Spring Boot backend for the Workify job portal project. This repository contains the server-side application (REST API) implemented using Java and Spring Boot, built with Maven.

## Contents

- `src/main/java/beworkify` - application source code (controllers, services, entities, repositories, configuration)
- `src/main/resources` - application configuration and email templates
- `pom.xml` - Maven build configuration
- `mvnw`, `mvnw.cmd` - Maven wrapper for Unix/Windows
- `Dockerfile`, `docker-compose.yml` - containerization and orchestration

## Prerequisites

- Java 17+ (or the version declared in the project POM)
- Git
- Docker & Docker Compose (optional, for containerized run)
- (Optional) An SMTP server or service if email features are used

## Quick start (development)

Run with the Maven wrapper (recommended):

```bash
# build
./mvnw clean package -DskipTests

# run
./mvnw spring-boot:run
```

After the app starts, the default HTTP port is typically 8080. Check `src/main/resources/application.yml` and `application-dev.yml` for profile-specific settings.

On Windows (Command Prompt / PowerShell) you can use `mvnw.cmd` instead:

```powershell
mvnw.cmd clean package -DskipTests
mvnw.cmd spring-boot:run
```

## Build and run the packaged JAR

```bash
./mvnw clean package
java -jar target/*.jar
```

## Running with Docker

Build the image (from project root):

```bash
docker build -t be-workify .
```

Start using docker-compose (if you use the provided `docker-compose.yml`):

```bash
docker-compose up --build
```

## Environment and configuration

Configuration files are under `src/main/resources`:

- `application.yml` - default config
- `application-dev.yml` - development profile
- `application-prod.yml` - production profile

Sensitive values (database credentials, JWT secrets, SMTP credentials) should be provided via environment variables or an external config when deploying. Do not commit secrets to version control.

## Tests

Run unit and integration tests with:

```bash
./mvnw test
```

## Logs and templates

Email templates are in `src/main/resources/templates` and `email-templates/`. Messages and localization files are in `messages.properties` and `messages_vi.properties`.

## Troubleshooting

- Port already in use: change `server.port` in `application.yml` or free the port.
- Database issues: check datasource configuration in `application-*.yml` and ensure the database is reachable.
- Missing env vars: follow startup logs to see which values are required (JWT secret, SMTP config, DB credentials).

## Where to look next (developer pointers)

- Security & JWT: `configuration/SecurityConfig`, `JwtAuthenticationFilter` and related classes
- Data initialization: `configuration/DataInitializer` and other initializers under `configuration`
- DTOs and mappings: `dto` and `mapper` packages

## Contributing

If you'd like to contribute, please:

1. Fork the repository
2. Create a branch for your feature/fix
3. Open a pull request with a clear description of changes

## License

Add your license here (if any).

---

If you want, I can also:

- add example .env or docker-compose overrides for local DB (Postgres/MySQL)
- add a short API reference (list of endpoints)
- generate a CONTRIBUTING.md or developer setup script

Tell me which extras you'd like and I'll add them.
