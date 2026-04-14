package com.pillmind.domain.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Gender")
class GenderTest {

    @Test
    @DisplayName("Should convert MALE string to MALE enum")
    void shouldConvertMaleString() {
        assertEquals(Gender.MALE, Gender.fromString("MALE"));
        assertEquals(Gender.MALE, Gender.fromString("male"));
        assertEquals(Gender.MALE, Gender.fromString("Male"));
        assertEquals(Gender.MALE, Gender.fromString("m"));
        assertEquals(Gender.MALE, Gender.fromString("masculino"));
    }

    @Test
    @DisplayName("Should convert FEMALE string to FEMALE enum")
    void shouldConvertFemaleString() {
        assertEquals(Gender.FEMALE, Gender.fromString("FEMALE"));
        assertEquals(Gender.FEMALE, Gender.fromString("female"));
        assertEquals(Gender.FEMALE, Gender.fromString("f"));
        assertEquals(Gender.FEMALE, Gender.fromString("feminino"));
    }

    @Test
    @DisplayName("Should convert OTHER string to OTHER enum")
    void shouldConvertOtherString() {
        assertEquals(Gender.OTHER, Gender.fromString("OTHER"));
        assertEquals(Gender.OTHER, Gender.fromString("other"));
        assertEquals(Gender.OTHER, Gender.fromString("o"));
        assertEquals(Gender.OTHER, Gender.fromString("outro"));
    }

    @Test
    @DisplayName("Should convert PREFER_NOT_TO_SAY string variants")
    void shouldConvertPreferNotToSayString() {
        assertEquals(Gender.PREFER_NOT_TO_SAY, Gender.fromString("PREFER_NOT_TO_SAY"));
        assertEquals(Gender.PREFER_NOT_TO_SAY, Gender.fromString("prefer not to say"));
        assertEquals(Gender.PREFER_NOT_TO_SAY, Gender.fromString("n"));
    }

    @Test
    @DisplayName("Should return null for null input")
    void shouldReturnNullForNullInput() {
        assertNull(Gender.fromString(null));
    }

    @Test
    @DisplayName("Should return null for blank input")
    void shouldReturnNullForBlankInput() {
        assertNull(Gender.fromString("  "));
    }

    @Test
    @DisplayName("Should return null for unknown value")
    void shouldReturnNullForUnknownValue() {
        assertNull(Gender.fromString("unknown-gender"));
    }

    @Test
    @DisplayName("Should have correct English labels")
    void shouldHaveCorrectEnglishLabels() {
        assertEquals("Male", Gender.MALE.getEnglishLabel());
        assertEquals("Female", Gender.FEMALE.getEnglishLabel());
        assertEquals("Other", Gender.OTHER.getEnglishLabel());
        assertEquals("Prefer not to say", Gender.PREFER_NOT_TO_SAY.getEnglishLabel());
    }

    @Test
    @DisplayName("Should have correct Portuguese labels")
    void shouldHaveCorrectPortugueseLabels() {
        assertEquals("Masculino", Gender.MALE.getPortugueseLabel());
        assertEquals("Feminino", Gender.FEMALE.getPortugueseLabel());
        assertEquals("Outro", Gender.OTHER.getPortugueseLabel());
        assertEquals("Prefiro não informar", Gender.PREFER_NOT_TO_SAY.getPortugueseLabel());
    }

    @Test
    @DisplayName("getValue should return name()")
    void getValueShouldReturnName() {
        assertEquals("MALE", Gender.MALE.getValue());
        assertEquals("FEMALE", Gender.FEMALE.getValue());
    }
}
