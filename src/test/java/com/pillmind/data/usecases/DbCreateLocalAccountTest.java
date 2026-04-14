package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.cryptography.Hasher;
import com.pillmind.data.protocols.db.LocalAccountRepository;
import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.ConflictException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.LocalAccount;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.CreateLocalAccount;

@DisplayName("DbCreateLocalAccount")
class DbCreateLocalAccountTest {

    private Hasher hasher;
    private UserRepository userRepository;
    private LocalAccountRepository localAccountRepository;
    private DbCreateLocalAccount sut;

    @BeforeEach
    void setUp() {
        hasher = mock(Hasher.class);
        userRepository = mock(UserRepository.class);
        localAccountRepository = mock(LocalAccountRepository.class);
        sut = new DbCreateLocalAccount(hasher, userRepository, localAccountRepository);
    }

    private User makeUser(String id, String name, String email) {
        return new User(id, name, email, LocalDate.of(1990, 1, 1), Gender.MALE, null);
    }

    private LocalAccount makeLocalAccount(String id, String userId, String email) {
        return new LocalAccount(id, userId, email, "hashed-password");
    }

    @Test
    @DisplayName("Should hash the password before persisting")
    void shouldHashPassword() {
        when(userRepository.emailExists(anyString())).thenReturn(false);
        when(localAccountRepository.emailExists(anyString())).thenReturn(false);
        when(hasher.hash("password123")).thenReturn("hashed-password");
        var savedUser = makeUser("user-id", "John Doe", "john@example.com");
        when(userRepository.add(any(User.class))).thenReturn(savedUser);
        when(localAccountRepository.add(any(LocalAccount.class)))
                .thenReturn(makeLocalAccount("local-id", "user-id", "john@example.com"));

        var params = new CreateLocalAccount.Params("John Doe", "john@example.com", "password123",
                LocalDate.of(1990, 1, 1), Gender.MALE, null);
        sut.execute(params);

        verify(hasher).hash("password123");
    }

    @Test
    @DisplayName("Should throw ConflictException when email already exists in UserRepository")
    void shouldThrowWhenEmailExistsInUserRepository() {
        when(userRepository.emailExists("existing@example.com")).thenReturn(true);

        var params = new CreateLocalAccount.Params("Name", "existing@example.com", "password",
                null, null, null);

        assertThrows(ConflictException.class, () -> sut.execute(params));
        verify(hasher, never()).hash(anyString());
        verify(userRepository, never()).add(any());
        verify(localAccountRepository, never()).add(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when email already exists in LocalAccountRepository")
    void shouldThrowWhenEmailExistsInLocalAccountRepository() {
        when(userRepository.emailExists("existing@example.com")).thenReturn(false);
        when(localAccountRepository.emailExists("existing@example.com")).thenReturn(true);

        var params = new CreateLocalAccount.Params("Name", "existing@example.com", "password",
                null, null, null);

        assertThrows(ConflictException.class, () -> sut.execute(params));
        verify(hasher, never()).hash(anyString());
        verify(userRepository, never()).add(any());
        verify(localAccountRepository, never()).add(any());
    }

    @Test
    @DisplayName("Should persist user and local account on success")
    void shouldPersistUserAndLocalAccountOnSuccess() {
        when(userRepository.emailExists(anyString())).thenReturn(false);
        when(localAccountRepository.emailExists(anyString())).thenReturn(false);
        when(hasher.hash(anyString())).thenReturn("hashed-password");
        var savedUser = makeUser("user-id", "John Doe", "john@example.com");
        when(userRepository.add(any(User.class))).thenReturn(savedUser);
        when(localAccountRepository.add(any(LocalAccount.class)))
                .thenReturn(makeLocalAccount("local-id", "user-id", "john@example.com"));

        var params = new CreateLocalAccount.Params("John Doe", "john@example.com", "password123",
                LocalDate.of(1990, 1, 1), Gender.MALE, null);
        var result = sut.execute(params);

        assertNotNull(result);
        assertNotNull(result.user());
        assertEquals("user-id", result.user().id());
        assertEquals("john@example.com", result.user().email());
        verify(userRepository).add(any(User.class));
        verify(localAccountRepository).add(any(LocalAccount.class));
    }

    @Test
    @DisplayName("Should return result with user and localAccountId")
    void shouldReturnResultWithUserAndLocalAccountId() {
        when(userRepository.emailExists(anyString())).thenReturn(false);
        when(localAccountRepository.emailExists(anyString())).thenReturn(false);
        when(hasher.hash(anyString())).thenReturn("hashed-password");
        var savedUser = makeUser("user-id", "Jane", "jane@example.com");
        when(userRepository.add(any(User.class))).thenReturn(savedUser);
        when(localAccountRepository.add(any(LocalAccount.class)))
                .thenReturn(makeLocalAccount("local-acc-id", "user-id", "jane@example.com"));

        var params = new CreateLocalAccount.Params("Jane", "jane@example.com", "secure123",
                LocalDate.of(1995, 6, 20), Gender.FEMALE, null);
        var result = sut.execute(params);

        assertNotNull(result.localAccountId());
        assertEquals(savedUser, result.user());
    }
}
