package com.example.api;

import com.example.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the contract of the generated API interface.
 * Note: This test depends on the OpenAPI generator running first.
 */
class UsersApiContractTest {

    @Test
    @DisplayName("Interface should define getUsers method")
    void shouldDefineGetUsersMethod() throws NoSuchMethodException {
        // Given
        Method getUsersMethod = UsersApi.class.getMethod("getUsers");

        // Then
        assertThat(getUsersMethod.getReturnType())
                .as("Return type should be ResponseEntity")
                .isEqualTo(ResponseEntity.class);

        assertThat(getUsersMethod.getGenericReturnType().getTypeName())
                .as("Should return List of Users")
                .contains("java.util.List<" + User.class.getName() + ">");
    }

    @Test
    @DisplayName("Interface should define createUser method")
    void shouldDefineCreateUserMethod() throws NoSuchMethodException {
        // Given
        Method createUserMethod = UsersApi.class.getMethod("createUser", User.class);

        // Then
        assertThat(createUserMethod.getReturnType())
                .as("Return type should be ResponseEntity")
                .isEqualTo(ResponseEntity.class);

        assertThat(createUserMethod.getGenericReturnType().getTypeName())
                .as("Should return single User")
                .contains(User.class.getName());

        assertThat(createUserMethod.getParameters()[0].getType())
                .as("Should accept User parameter")
                .isEqualTo(User.class);
    }

    @Test
    @DisplayName("Interface should have proper package")
    void shouldHaveProperPackage() {
        assertThat(UsersApi.class.getPackage().getName())
                .as("Interface should be in the correct package")
                .isEqualTo("com.example.api");
    }

    @Test
    @DisplayName("Interface should be public")
    void shouldBePublic() {
        assertThat(UsersApi.class.getModifiers())
                .as("Interface should be public")
                .isEqualTo(java.lang.reflect.Modifier.PUBLIC | java.lang.reflect.Modifier.INTERFACE | java.lang.reflect.Modifier.ABSTRACT);
    }
}