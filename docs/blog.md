# 🚀 Streamlining API Development with OpenAPI and Spring Boot: A Hands-on Guide

Are you tired of maintaining API documentation that's always out of sync with your code? Looking for a way to ensure your API implementation strictly follows your API specification? In this post, we'll explore a powerful approach to API development using OpenAPI specifications and code generation in a Spring Boot application.

## Introduction: Putting Contracts First for Better APIs 📋
Imagine starting an API project where the blueprint comes first, and everything else—implementation, client SDKs, and even documentation—flows naturally from there. That’s the beauty of contract-first API development.

By defining your API contract upfront, you unlock a wealth of benefits:

- Teams can work in parallel without stepping on each other’s toes.
- The API behaves consistently across environments and versions.
- You can auto-generate client SDKs, reducing manual effort.
- Documentation stays accurate and up-to-date—effortlessly!

This guide walks you through a practical example of contract-first development using a multi-module Maven project to build a user management API and see how OpenAPI brings it all together seamlessly.

## Project Architecture 🏗️

Our project consists of two main modules:

1. `user-management-api-spec`: Houses the OpenAPI specification and generates interface code
2. `user-management-service`: Implements the API using the generated interfaces

```
├── user-management-api-spec
│   ├── src/main/resources
│   │   └── user-management-api.yaml
│   └── pom.xml
├── user-management-service
│   ├── src/main/java
│   │   └── com/example/usermanagement
│   └── pom.xml
└── pom.xml
```

## The OpenAPI Specification: Defining Our Contract 📝

Let's start by examining our API specification in `user-management-api.yaml`:

```yaml
openapi: 3.0.3
info:
  title: User Management API
  description: REST API for User Management operations
  version: 1.0.0
servers:
  - url: <http://localhost:8080/api/v1>
    description: Local development server

paths:
  /users:
    get:
      tags:
        - Users
      summary: Retrieve users
      operationId: getUsers
      responses:
        '200':
          description: Users retrieved successfully
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/User'

components:
  schemas:
    User:
      type: object
      properties:
        id:
          type: integer
          format: int64
          readOnly: true
        name:
          type: string
          minLength: 1
          maxLength: 100
          pattern: '^[a-zA-Z0-9\\s.-]+$'
        email:
          type: string
          format: email
          pattern: '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'
      required:
        - name
        - email
```

## Setting Up Code Generation 🛠️

The `user-management-api-spec/pom.xml` configures the OpenAPI generator plugin:

```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>${openapi-generator.version}</version>
    <executions>
        <execution>
            <id>generate-api</id>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/user-management-api.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <apiPackage>com.example.api</apiPackage>
                <modelPackage>com.example.model</modelPackage>
                <configOptions>
                    <sourceFolder>src/gen/java/main</sourceFolder>
                    <useSpringBoot3>true</useSpringBoot3>
                    <interfaceOnly>true</interfaceOnly>
                    <useTags>true</useTags>
                    <documentationProvider>none</documentationProvider>
                    <annotationLibrary>none</annotationLibrary>
                    <useLombok>true</useLombok>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

## Implementing the Generated Interface 💻

In the `user-management-service` module, we create a controller that implements the generated interface:

```java
@RestController
public class UsersController implements UsersApi {

    // Using CopyOnWriteArrayList for thread-safety in this demo
    private final List<User> users = new CopyOnWriteArrayList<>();

    @Override
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(new ArrayList<>(users));
    }

    @Override
    public ResponseEntity<User> createUser(@Valid User user) {
        // Demo implementation of email uniqueness check
        if (users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            return ResponseEntity.badRequest().build();
        }

        user.setId((long) (users.size() + 1));
        users.add(user);
        return ResponseEntity.status(201).body(user);
    }
}
```

## Comprehensive Testing Strategy 🧪

Our testing approach covers multiple aspects:

### 1. Schema Validation Tests

```java
@Test
@DisplayName("User schema should have required fields")
void shouldHaveRequiredFields() {
    Schema<?> userSchema = openAPI.getComponents().getSchemas().get("User");

    assertThat(userSchema.getRequired())
        .as("Required fields should be defined")
        .containsExactlyInAnyOrder("name", "email");
}
```

### 2. Contract Tests

```java
@Test
@DisplayName("Interface should define getUsers method")
void shouldDefineGetUsersMethod() throws NoSuchMethodException {
    Method getUsersMethod = UsersApi.class.getMethod("getUsers");

    assertThat(getUsersMethod.getReturnType())
        .as("Return type should be ResponseEntity")
        .isEqualTo(ResponseEntity.class);

    assertThat(getUsersMethod.getGenericReturnType().getTypeName())
        .as("Should return List of Users")
        .contains("java.util.List<" + User.class.getName() + ">");
}
```

### 3. Model Validation Tests

```java
@Test
@DisplayName("Should accept valid email")
void shouldAcceptValidEmail() {
    User user = new User().name("John Doe").email("john.doe@example.com");
    var violations = validator.validateProperty(user, "email");
    assertThat(violations).isEmpty();
}
```

## Application Configuration ⚙️

The `application.yml` in the service module configures the application:

```yaml
server:
  servlet:
    context-path: /api/v1

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

logging:
  level:
    com.example: INFO
```

## Key Benefits of This Approach 🎯

1. **Type Safety and Compile-Time Checks**
    - The generated interfaces ensure type-safe implementation
    - Compilation fails if implementation doesn't match the specification
2. **Clear Separation of Concerns**
    - API specification is isolated from implementation
    - Changes to the API contract are explicit and versioned
3. **Automated Documentation**
    - Swagger UI is automatically generated and always up-to-date
    - OpenAPI specification serves as living documentation
4. **Development Workflow Improvements**
    - Frontend teams can start development using the API specification
    - Backend teams can implement against a stable interface
    - Changes to the API are explicit and tracked in version control

## Production Considerations 🚨

When adapting this approach for production:

1. **Persistence Layer**
    - Replace in-memory storage with proper database integration
    - Implement proper transaction management
2. **Security**
    - Add authentication and authorization
    - Implement rate limiting
    - Add security headers
3. **Error Handling**
    - Implement global exception handling
    - Add proper error responses
    - Include validation messages
4. **Monitoring and Logging**
    - Add proper logging framework
    - Implement metrics collection
    - Set up health checks

## Source Code 📦

The complete source code for this example is available on GitHub:
[springboot-openapi-codegen-demo](https://github.com/sandeepkv93/springboot-openapi-codegen-demo)

## Final Thoughts 💭

This approach to API development combines the best practices of contract-first development with the type safety of generated code. It provides a robust foundation for building maintainable and scalable APIs while ensuring consistency between documentation and implementation.

The separation between API specification and implementation allows teams to work efficiently while maintaining a single source of truth for the API contract. The generated code provides compile-time safety, while the OpenAPI specification serves as both documentation and a development contract.