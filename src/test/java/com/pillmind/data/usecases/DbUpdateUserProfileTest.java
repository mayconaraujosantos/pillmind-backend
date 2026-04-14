package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.UpdateUserProfile;

@DisplayName("DbUpdateUserProfile")
class DbUpdateUserProfileTest {

    private UserRepository userRepository;
    private DbUpdateUserProfile sut;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        sut = new DbUpdateUserProfile(userRepository);
    }

    private User makeUser(String id, String name, String email) {
        return new User(id, name, email, LocalDate.of(1990, 1, 1), Gender.MALE, null);
    }

    @Test
    @DisplayName("Should update and return user profile")
    void shouldUpdateUserProfile() {
        var existing = makeUser("user-id", "Old Name", "old@example.com");
        var updated = makeUser("user-id", "New Name", "new@example.com");

        when(userRepository.findById("user-id")).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.update(any(User.class))).thenReturn(updated);

        var params = new UpdateUserProfile.Params("user-id", "New Name", "new@example.com",
                LocalDate.of(1990, 1, 1), Gender.MALE, null);

        var result = sut.execute(params);

        assertNotNull(result);
        assertEquals("New Name", result.name());
        verify(userRepository).update(any(User.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when user does not exist")
    void shouldThrowNotFoundWhenUserNotFound() {
        when(userRepository.findById("unknown-id")).thenReturn(Optional.empty());

        var params = new UpdateUserProfile.Params("unknown-id", "Name", "email@example.com",
                null, null, null);

        assertThrows(NotFoundException.class, () -> sut.execute(params));
        verify(userRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when new email is taken by another user")
    void shouldThrowConflictWhenEmailTakenByOtherUser() {
        var existing = makeUser("user-id", "Name", "old@example.com");
        var otherUser = makeUser("other-id", "Other", "taken@example.com");

        when(userRepository.findById("user-id")).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(otherUser));

        var params = new UpdateUserProfile.Params("user-id", "Name", "taken@example.com",
                null, null, null);

        assertThrows(ConflictException.class, () -> sut.execute(params));
        verify(userRepository, never()).update(any());
    }

    @Test
    @DisplayName("Should allow keeping same email on update")
    void shouldAllowKeepingSameEmail() {
        var existing = makeUser("user-id", "Name", "same@example.com");
        var updated = makeUser("user-id", "New Name", "same@example.com");

        when(userRepository.findById("user-id")).thenReturn(Optional.of(existing));
        when(userRepository.update(any(User.class))).thenReturn(updated);

        var params = new UpdateUserProfile.Params("user-id", "New Name", "same@example.com",
                LocalDate.of(1990, 1, 1), Gender.MALE, null);

        var result = sut.execute(params);

        assertNotNull(result);
        verify(userRepository).update(any(User.class));
        // findByEmail should NOT be called when email is unchanged
        verify(userRepository, never()).findByEmail(any());
    }
}
