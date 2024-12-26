package com.example.api;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAPISchemaValidationTest {

    private final OpenAPI openAPI;

    OpenAPISchemaValidationTest() throws IOException {
        ClassPathResource resource = new ClassPathResource("user-management-api.yaml");

        ParseOptions options = new ParseOptions();
        options.setResolve(true);
        options.setResolveFully(true);

        try (InputStream inputStream = resource.getInputStream()) {
            SwaggerParseResult result = new OpenAPIParser().readContents(
                    new String(inputStream.readAllBytes()),
                    null,
                    options
            );

            assertThat(result.getMessages())
                    .as("OpenAPI parsing should not have any errors")
                    .isEmpty();

            this.openAPI = result.getOpenAPI();
        }
    }

    @Test
    @DisplayName("OpenAPI specification should have basic information")
    void shouldHaveBasicInfo() {
        assertThat(openAPI.getInfo().getTitle())
                .as("API title should be set")
                .isEqualTo("User Management API");

        assertThat(openAPI.getInfo().getVersion())
                .as("API version should be set")
                .isEqualTo("1.0.0");

        assertThat(openAPI.getServers())
                .as("API should have server configuration")
                .isNotEmpty();
    }

    @Nested
    @DisplayName("User Endpoints")
    class UserEndpoints {

        @Test
        @DisplayName("GET /users endpoint should be properly defined")
        void shouldHaveGetUsersEndpoint() {
            // Given
            PathItem usersPath = openAPI.getPaths().get("/users");
            Operation getOperation = usersPath.getGet();

            // Then
            assertThat(getOperation)
                    .as("/users should have GET operation")
                    .isNotNull();

            assertThat(getOperation.getOperationId())
                    .as("Operation ID should be set")
                    .isEqualTo("getUsers");

            assertThat(getOperation.getResponses())
                    .as("Should have response definitions")
                    .containsKeys("200", "500");

            ApiResponse successResponse = getOperation.getResponses().get("200");
            assertThat(successResponse.getContent())
                    .as("Success response should have content")
                    .containsKey("application/json");
        }

        @Test
        @DisplayName("POST /users endpoint should be properly defined")
        void shouldHavePostUsersEndpoint() {
            // Given
            PathItem usersPath = openAPI.getPaths().get("/users");
            Operation postOperation = usersPath.getPost();

            // Then
            assertThat(postOperation)
                    .as("/users should have POST operation")
                    .isNotNull();

            assertThat(postOperation.getOperationId())
                    .as("Operation ID should be set")
                    .isEqualTo("createUser");

            assertThat(postOperation.getResponses())
                    .as("Should have response definitions")
                    .containsKeys("201", "400", "409", "500");

            assertThat(postOperation.getRequestBody())
                    .as("Should have request body")
                    .isNotNull();

            assertThat(postOperation.getRequestBody().getRequired())
                    .as("Request body should be required")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("User Schema")
    class UserSchema {

        @Test
        @DisplayName("User schema should have required fields")
        void shouldHaveRequiredFields() {
            Schema<?> userSchema = openAPI.getComponents().getSchemas().get("User");

            assertThat(userSchema.getRequired())
                    .as("Required fields should be defined")
                    .containsExactlyInAnyOrder("name", "email");
        }

        @Test
        @DisplayName("User schema should have proper property definitions")
        void shouldHaveProperPropertyDefinitions() {
            Schema<?> userSchema = openAPI.getComponents().getSchemas().get("User");
            Map<String, Schema> properties = userSchema.getProperties();

            // ID property
            Schema<?> idProperty = properties.get("id");
            assertThat(idProperty.getReadOnly())
                    .as("ID should be read-only")
                    .isTrue();

            // Name property
            Schema<?> nameProperty = properties.get("name");
            assertThat(nameProperty.getMinLength())
                    .as("Name should have minimum length")
                    .isEqualTo(1);
            assertThat(nameProperty.getMaxLength())
                    .as("Name should have maximum length")
                    .isEqualTo(100);
            assertThat(nameProperty.getPattern())
                    .as("Name should have pattern")
                    .isEqualTo("^[a-zA-Z0-9\\s.-]+$");

            // Email property
            Schema<?> emailProperty = properties.get("email");
            assertThat(emailProperty.getFormat())
                    .as("Email should have proper format")
                    .isEqualTo("email");
            assertThat(emailProperty.getPattern())
                    .as("Email should have pattern")
                    .isEqualTo("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        }
    }

    @Test
    @DisplayName("API should define error responses")
    void shouldDefineErrorResponses() {
        PathItem usersPath = openAPI.getPaths().get("/users");

        // GET error responses
        Map<String, ApiResponse> getResponses = usersPath.getGet().getResponses();
        assertThat(getResponses.get("500"))
                .as("GET should have 500 error response")
                .isNotNull();

        // POST error responses
        Map<String, ApiResponse> postResponses = usersPath.getPost().getResponses();
        assertThat(postResponses)
                .as("POST should have proper error responses")
                .containsKeys("400", "409", "500");
    }
}