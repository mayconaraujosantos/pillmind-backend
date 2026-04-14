package com.pillmind.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User")
class UserTest {

    @Test
    @DisplayName("Should create user with minimal constructor")
    void shouldCreateUserWithMinimalConstructor() {
        var user = new User("user-id", "John Doe", "john@example.com");

        assertEquals("user-id", user.id());
        assertEquals("John Doe", user.name());
        assertEquals("john@example.com", user.email());
        assertNull(user.dateOfBirth());
        assertNull(user.gender());
        assertNull(user.pictureUrl());
        assertFalse(user.emailVerified());
        assertNotNull(user.createdAt());
        assertNotNull(user.updatedAt());
    }

    @Test
    @DisplayName("Should create user with full constructor")
    void shouldCreateUserWithFullConstructor() {
        var dob = LocalDate.of(1990, 5, 15);
        var user = new User("user-id", "Jane", "jane@example.com", dob, Gender.FEMALE, "pic.jpg");

        assertEquals("Jane", user.name());
        assertEquals(dob, user.dateOfBirth());
        assertEquals(Gender.FEMALE, user.gender());
        assertEquals("pic.jpg", user.pictureUrl());
        assertFalse(user.emailVerified());
    }

    @Test
    @DisplayName("Should implement Entity interface")
    void shouldImplementEntityInterface() {
        var user = new User("id", "Name", "email@example.com");
        assertTrue(user instanceof Entity);
    }

    @Test
    @DisplayName("isProfileComplete should return true when all required fields present")
    void isProfileCompleteShouldReturnTrueWhenComplete() {
        var user = new User("id", "John", "john@example.com",
                LocalDate.of(1990, 1, 1), Gender.MALE, null);
        assertTrue(user.isProfileComplete());
    }

    @Test
    @DisplayName("isProfileComplete should return false when name is missing")
    void isProfileCompleteShouldReturnFalseWhenNameMissing() {
        var user = new User("id", null, "john@example.com",
                LocalDate.of(1990, 1, 1), Gender.MALE, null);
        assertFalse(user.isProfileComplete());
    }

    @Test
    @DisplayName("isProfileComplete should return false when dateOfBirth is missing")
    void isProfileCompleteShouldReturnFalseWhenDobMissing() {
        var user = new User("id", "John", "john@example.com", null, Gender.MALE, null);
        assertFalse(user.isProfileComplete());
    }

    @Test
    @DisplayName("isProfileComplete should return false when gender is missing")
    void isProfileCompleteShouldReturnFalseWhenGenderMissing() {
        var user = new User("id", "John", "john@example.com",
                LocalDate.of(1990, 1, 1), null, null);
        assertFalse(user.isProfileComplete());
    }

    @Test
    @DisplayName("withUpdatedProfile should return updated user preserving id")
    void withUpdatedProfileShouldReturnUpdatedUser() {
        var user = new User("user-id", "Old Name", "old@example.com",
                LocalDate.of(1990, 1, 1), Gender.MALE, null);
        var updated = user.withUpdatedProfile("New Name", "new@example.com",
                LocalDate.of(1990, 1, 1), Gender.FEMALE, "new-pic.jpg");

        assertEquals("user-id", updated.id());
        assertEquals("New Name", updated.name());
        assertEquals("new@example.com", updated.email());
        assertEquals(Gender.FEMALE, updated.gender());
        assertEquals("new-pic.jpg", updated.pictureUrl());
    }

    @Test
    @DisplayName("withEmailVerified should update emailVerified flag")
    void withEmailVerifiedShouldUpdateFlag() {
        var user = new User("id", "John", "john@example.com");
        assertFalse(user.emailVerified());

        var verified = user.withEmailVerified(true);
        assertTrue(verified.emailVerified());
        assertEquals("id", verified.id());
    }

    @Test
    @DisplayName("withUpdatedProfile(name, pictureUrl) should update only name and picture")
    void withUpdatedProfileNameAndPictureShouldPreserveOtherFields() {
        var dob = LocalDate.of(1990, 5, 15);
        var user = new User("id", "Old", "user@example.com", dob, Gender.MALE, "old-pic.jpg",
                true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());

        var updated = user.withUpdatedProfile("New", "new-pic.jpg");

        assertEquals("New", updated.name());
        assertEquals("new-pic.jpg", updated.pictureUrl());
        assertEquals("user@example.com", updated.email());
        assertEquals(dob, updated.dateOfBirth());
        assertEquals(Gender.MALE, updated.gender());
    }
}
