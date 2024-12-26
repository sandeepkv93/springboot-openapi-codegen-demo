package com.example.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {
    private static Validator validator;

    @BeforeAll
    static void setupValidatorInstance() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidation {
        @Test
        @DisplayName("Should accept valid email")
        void shouldAcceptValidEmail() {
            // Given
            User user = new User()
                    .name("John Doe")
                    .email("john.doe@example.com");

            // When
            var violations = validator.validateProperty(user, "email");

            // Then
            assertThat(violations).isEmpty();
        }

        @ParameterizedTest
        @DisplayName("Should reject invalid emails")
        @ValueSource(strings = {
                "",
                "invalid",
                "@example.com",
                "john@",
                "john@.com",
                "@.com"
        })
        void shouldRejectInvalidEmails(String invalidEmail) {
            // Given
            User user = new User()
                    .name("John Doe")
                    .email(invalidEmail);

            // When
            var violations = validator.validateProperty(user, "email");

            // Then
            assertThat(violations).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Name Validation")
    class NameValidation {
        @Test
        @DisplayName("Should accept valid name")
        void shouldAcceptValidName() {
            // Given
            User user = new User()
                    .name("John Doe")
                    .email("john.doe@example.com");

            // When
            var violations = validator.validateProperty(user, "name");

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should reject empty name")
        void shouldRejectEmptyName() {
            // Given
            User user = new User()
                    .name("")
                    .email("john.doe@example.com");

            // When
            var violations = validator.validateProperty(user, "name");

            // Then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("Should reject too long name")
        void shouldRejectTooLongName() {
            // Given
            String longName = "a".repeat(101);
            User user = new User()
                    .name(longName)
                    .email("john.doe@example.com");

            // When
            var violations = validator.validateProperty(user, "name");

            // Then
            assertThat(violations).isNotEmpty();
        }
    }

    @Test
    @DisplayName("Should build valid user")
    void shouldBuildValidUser() {
        // Given
        User user = new User()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com");

        // When
        var violations = validator.validate(user);

        // Then
        assertThat(violations).isEmpty();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
    }
}
