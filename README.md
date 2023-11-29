# Spring Boot Microservice Skeleton

This is a skeleton for a Spring Boot microservice. It is intended to be used as a starting point for new microservices.

## Pre-requisites
- Java 17
- Spring Boot 3.x
- Gradle 8.x
- Docker

## Features
- Reactive Web (WebFlux)
- OpenAPI 3.x documentation & Swagger UI
- Health check endpoint
- Logback logging
- Dockerized (multi-stage build)
- Checkstyle
- OWASP Dependency Check

## Dependencies

## Getting Started

### Initial Project Configuration Changes
1. Clone this repository
2. Rename the project to your project name
3. Rename `rootProject.name` in `settings.gradle.kts` to your project name

### Docker Configuration Changes
1. Update `Dokerfile` to use the correct jar file name `ENTRYPOINT ["java", "-jar", "/app/demo-0.0.1SNAPSHOT.jar"]`.
2. Update `Dockerfile` Gradle tag to the latest version [here](https://hub.docker.com/_/gradle).
3. Update `Dockerfile` Java tag to the latest version [here](https://hub.docker.com/_/openjdk).

## Profiles

* **default**: This is the default profile that contains settings applicable to all environments. It serves as a baseline configuration for your application.

* **dev**: The dev profile is typically used for development environments. It may contain configurations specific to development, such as a local database setup, debugging options, and other settings that make development easier.

* **test**: The test profile is usually used for automated testing environments, like unit tests and integration tests. It can have configurations for in-memory databases or test-specific configurations that differ from the production setup.

* **prod**: The prod profile is meant for production environments. It contains configurations optimized for performance, security, and reliability. This is where your application runs in a real-world scenario.

## JaCoCo

Adding exclusions through `build.gradle`:

```
tasks.jacocoTestReport {
	dependsOn(tasks.test)
	...
	afterEvaluate {
		classDirectories.setFrom(files(classDirectories.files.collect {
			fileTree(dir: it, exclude: [
				'**/SkeletonApplication.class',
			])
		}))
	}
}
```

## Documentation

### Api Docs 
`http://localhost:8081/api-docs`
### Swagger-ui 
`http://localhost:8081/swagger-ui`

## Metrics

### Health Check
`http://localhost:8081/health`

## Metrics
`http://localhost:8081/metrics`

## Logging
`http://localhost:8081/loggers`

## Best Practices

### Design
#### Basic-Required
- [ ] Backend for Frontend (BFF) as an API Gateway
- [ ] Database schema per service
- [x] Expose the /health endpoint
#### Medium
- [ ] Injection of external configuration at runtime
- [ ] Service Contracts Testing Agreement
#### Advanced
- [ ] Liquibase for database change management
- [ ] Dapr for event-driven programming
- [ ] Implement Distributed Tracing
- [ ] If High Performance is Required, Spring Native
- [ ] Early and Frequent DevSecOps

### Programming
#### Basic-Required
- [ ] Development tools
- [ ] Project structure
- [ ] Logging rules
- [ ] Startup Profiles Configuration
- [ ] Code documentation with Open API
- [ ] Use of UI Swagger
- [ ] Input validation in controllers
- [ ] Use of Lombok
- [ ] Use of Checkstyle
#### Medium
- [ ] Project dependency standards
- [ ] Simple and clean controller layer
- [ ] Service layer focused on business logic
- [ ] Constructor injection instead of @Autowired
- [ ] Pagination and sorting with Spring Data JPA
- [ ] Unit testing with JUnit and Mockito
#### Advanced
- [ ] JSON log configuration with Logback
- [ ] Global exception handling
- [ ] Avoid unnecessary additional dependencies
- [ ] Review Dependency Updates
- [ ] Use of Maven Wrapper