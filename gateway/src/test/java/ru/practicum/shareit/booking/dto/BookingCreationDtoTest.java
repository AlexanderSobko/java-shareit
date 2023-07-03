package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
class BookingCreationDtoTest {

    private BookingCreationDto creationDto;
    @Autowired
    private JacksonTester<BookingCreationDto> json;

    @BeforeEach
    void beforeEach() {
        creationDto = new BookingCreationDto(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), 1, 1);
    }

    @Test
    void isValidRange() {
        assertFalse(creationDto.isValidRange());
        creationDto.setEnd(null);
        assertTrue(creationDto.isValidRange());
    }

    @Test
    void testSerialize() throws IOException {
        JsonContent<BookingCreationDto> result = json.write(creationDto);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(creationDto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(creationDto.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.userId", creationDto.getUserId(), Long.class);
        assertThat(result).extractingJsonPathNumberValue("$.iteId", creationDto.getItemId(), Long.class);
    }
}