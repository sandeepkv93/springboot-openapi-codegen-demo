# SpringBoot OpenAPI Code Generation Demo

This project demonstrates a contract-first approach to API development using OpenAPI specifications and Spring Boot. It showcases how to effectively use code generation to maintain consistency between API documentation, implementation, and testing.

[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![OpenAPI Generator](https://img.shields.io/badge/OpenAPI%20Generator-7.0.1-orange.svg)](https://openapi-generator.tech)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

## 🎯 Features

- **Contract-First API Development**: OpenAPI specification drives the development process
- **Code Generation**: Automated generation of API interfaces and models
- **Type Safety**: Compile-time verification of API implementation
- **Comprehensive Testing**: Multiple testing layers including contract and schema validation
- **Modern Stack**: Spring Boot 3.2, Java 21, Maven
- **Documentation**: Auto-generated Swagger UI
- **Validation**: Built-in request/response validation
- **Clean Architecture**: Multi-module project structure

## 🏗️ Project Structure

```
└── sandeepkv93-springboot-openapi-codegen-demo/
    ├── user-management-api-spec/    # OpenAPI specification and generated code
    │   ├── src/
    │   │   ├── main/resources/
    │   │   │   └── user-management-api.yaml
    │   │   └── test/
    │   │       └── java/           # API contract tests
    │   └── pom.xml
    ├── user-management-service/     # Service implementation
    │   ├── src/
    │   │   └── main/
    │   │       ├── java/
    │   │       │   └── com/example/usermanagement/
    │   │       └── resources/
    │   │           └── application.yml
    │   └── pom.xml
    └── pom.xml
```

## 🚀 Getting Started

### Prerequisites

- JDK 17
- Maven 3.8+
- Your favorite IDE (IntelliJ IDEA recommended)

### Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/sandeepkv93/springboot-openapi-codegen-demo.git
   cd springboot-openapi-codegen-demo
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   cd user-management-service
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080/api/v1`

### API Documentation

Once the application is running, you can access:
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/api/v1/api-docs`

## 🔍 Key Components

### OpenAPI Specification

Located at `user-management-api-spec/src/main/resources/user-management-api.yaml`, this is the source of truth for our API. It defines:

- Available endpoints
- Request/response structures
- Data validations
- Error responses

### Code Generation

The OpenAPI Generator Maven plugin is configured in `user-management-api-spec/pom.xml`:

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <configuration>
        <inputSpec>${project.basedir}/src/main/resources/user-management-api.yaml</inputSpec>
        <generatorName>spring</generatorName>
        <apiPackage>com.example.api</apiPackage>
        <modelPackage>com.example.model</modelPackage>
        <configOptions>
            <useSpringBoot3>true</useSpringBoot3>
            <interfaceOnly>true</interfaceOnly>
        </configOptions>
    </configuration>
</plugin>
```

### API Implementation

The service implementation in `user-management-service` shows how to:
- Implement generated interfaces
- Handle validation
- Manage responses
- Structure the application

Example controller implementation:

```java
@RestController
public class UsersController implements UsersApi {
    private final List<User> users = new CopyOnWriteArrayList<>();

    @Override
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(new ArrayList<>(users));
    }

    @Override
    public ResponseEntity<User> createUser(@Valid User user) {
        // Implementation details...
    }
}
```

## 🧪 Testing Strategy

The project includes multiple testing layers:

### 1. Contract Tests
```java
@Test
void shouldDefineGetUsersMethod() throws NoSuchMethodException {
    Method getUsersMethod = UsersApi.class.getMethod("getUsers");
    assertThat(getUsersMethod.getReturnType()).isEqualTo(ResponseEntity.class);
}
```

### 2. Schema Validation Tests
```java
@Test
void shouldHaveRequiredFields() {
    Schema<?> userSchema = openAPI.getComponents().getSchemas().get("User");
    assertThat(userSchema.getRequired())
        .containsExactlyInAnyOrder("name", "email");
}
```

### 3. Model Validation Tests
```java
@Test
void shouldAcceptValidEmail() {
    User user = new User().name("John Doe").email("john.doe@example.com");
    var violations = validator.validateProperty(user, "email");
    assertThat(violations).isEmpty();
}
```

## 📝 Production Considerations

For production deployment, consider implementing:

1. **Security**
    - Authentication and authorization
    - API rate limiting
    - Security headers

2. **Persistence**
    - Database integration
    - Transaction management
    - Connection pooling

3. **Monitoring**
    - Health checks
    - Metrics collection
    - Logging framework

4. **Error Handling**
    - Global exception handling
    - Proper error messages
    - Validation responses

## 🛠️ Development Workflow

1. Update the OpenAPI specification (`user-management-api.yaml`)
2. Generate the code: `mvn clean generate-sources`
3. Implement the generated interfaces
4. Add tests for new functionality
5. Run tests: `mvn test`
6. Build and run the application