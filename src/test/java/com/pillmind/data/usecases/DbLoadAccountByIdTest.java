package com.pillmind.data.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pillmind.data.protocols.db.LoadAccountByIdRepository;
import com.pillmind.domain.errors.NotFoundException;
import com.pillmind.domain.models.Account;
import com.pillmind.domain.usecases.LoadAccountById;

@DisplayName("DbLoadAccountById")
class DbLoadAccountByIdTest {

    private LoadAccountByIdRepository loadAccountByIdRepository;
    private DbLoadAccountById sut;

    @BeforeEach
    void setUp() {
        loadAccountByIdRepository = mock(LoadAccountByIdRepository.class);
        sut = new DbLoadAccountById(loadAccountByIdRepository);
    }

    @Test
    @DisplayName("Should return account when found by id")
    void shouldReturnAccountWhenFound() {
        var account = new Account("account-id", "John Doe", "john@example.com", "hashed", false);
        when(loadAccountByIdRepository.loadById("account-id")).thenReturn(Optional.of(account));

        var result = sut.execute(new LoadAccountById.Params("account-id"));

        assertNotNull(result);
        assertEquals("account-id", result.id());
        assertEquals("john@example.com", result.email());
    }

    @Test
    @DisplayName("Should throw NotFoundException when account is not found")
    void shouldThrowNotFoundWhenAccountNotFound() {
        when(loadAccountByIdRepository.loadById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> sut.execute(new LoadAccountById.Params("nonexistent")));
    }
}
