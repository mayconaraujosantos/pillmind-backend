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
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.cryptography.Encrypter;
import com.pillmind.data.protocols.cryptography.HashComparer;
import com.pillmind.data.protocols.db.LocalAccountRepository;
import com.pillmind.data.protocols.db.UserRepository;
import com.pillmind.domain.errors.UnauthorizedException;
import com.pillmind.domain.models.Gender;
import com.pillmind.domain.models.LocalAccount;
import com.pillmind.domain.models.User;
import com.pillmind.domain.usecases.LocalAuthentication;

@DisplayName("DbLocalAuthentication")
class DbLocalAuthenticationTest {

    private LocalAccountRepository localAccountRepository;
    private UserRepository userRepository;
    private HashComparer hashComparer;
    private Encrypter encrypter;
    private DbLocalAuthentication sut;

    @BeforeEach
    void setUp() {
        localAccountRepository = mock(LocalAccountRepository.class);
        userRepository = mock(UserRepository.class);
        hashComparer = mock(HashComparer.class);
        encrypter = mock(Encrypter.class);
        sut = new DbLocalAuthentication(localAccountRepository, userRepository, hashComparer, encrypter);
    }

    private LocalAccount makeLocalAccount(String id, String userId, String email) {
        return new LocalAccount(id, userId, email, "hashed-password");
    }

    private User makeUser(String id, String name, String email) {
        return new User(id, name, email, LocalDate.of(1990, 1, 1), Gender.MALE, null);
    }

    @Test
    @DisplayName("Should return access token and user on valid credentials")
    void shouldReturnAccessTokenOnValidCredentials() {
        var localAccount = makeLocalAccount("local-id", "user-id", "john@example.com");
        var user = makeUser("user-id", "John Doe", "john@example.com");

        when(localAccountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(localAccount));
        when(hashComparer.compare("password123", "hashed-password")).thenReturn(true);
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(localAccountRepository.update(any(LocalAccount.class))).thenReturn(localAccount);
        when(encrypter.encrypt("user-id")).thenReturn("access-token-xyz");

        var params = new LocalAuthentication.Params("john@example.com", "password123");
        var result = sut.execute(params);

        assertNotNull(result);
        assertEquals("access-token-xyz", result.accessToken());
        assertEquals("user-id", result.user().id());
        verify(encrypter).encrypt("user-id");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when email is not found")
    void shouldThrowWhenEmailNotFound() {
        when(localAccountRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        var params = new LocalAuthentication.Params("notfound@example.com", "password");

        assertThrows(UnauthorizedException.class, () -> sut.execute(params));
        verify(hashComparer, never()).compare(anyString(), anyString());
        verify(encrypter, never()).encrypt(anyString());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when password is incorrect")
    void shouldThrowWhenPasswordIsIncorrect() {
        var localAccount = makeLocalAccount("local-id", "user-id", "john@example.com");

        when(localAccountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(localAccount));
        when(hashComparer.compare("wrong-password", "hashed-password")).thenReturn(false);

        var params = new LocalAuthentication.Params("john@example.com", "wrong-password");

        assertThrows(UnauthorizedException.class, () -> sut.execute(params));
        verify(encrypter, never()).encrypt(anyString());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user profile not found")
    void shouldThrowWhenUserNotFound() {
        var localAccount = makeLocalAccount("local-id", "user-id", "john@example.com");

        when(localAccountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(localAccount));
        when(hashComparer.compare("password123", "hashed-password")).thenReturn(true);
        when(userRepository.findById("user-id")).thenReturn(Optional.empty());
        when(localAccountRepository.update(any(LocalAccount.class))).thenReturn(localAccount);

        var params = new LocalAuthentication.Params("john@example.com", "password123");

        assertThrows(UnauthorizedException.class, () -> sut.execute(params));
        verify(encrypter, never()).encrypt(anyString());
    }

    @Test
    @DisplayName("Should update lastLoginAt after successful authentication")
    void shouldUpdateLastLoginAtOnSuccess() {
        var localAccount = makeLocalAccount("local-id", "user-id", "john@example.com");
        var user = makeUser("user-id", "John Doe", "john@example.com");

        when(localAccountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(localAccount));
        when(hashComparer.compare("password123", "hashed-password")).thenReturn(true);
        when(userRepository.findById("user-id")).thenReturn(Optional.of(user));
        when(localAccountRepository.update(any(LocalAccount.class))).thenReturn(localAccount);
        when(encrypter.encrypt("user-id")).thenReturn("token");

        var params = new LocalAuthentication.Params("john@example.com", "password123");
        sut.execute(params);

        verify(localAccountRepository).update(any(LocalAccount.class));
    }
}
