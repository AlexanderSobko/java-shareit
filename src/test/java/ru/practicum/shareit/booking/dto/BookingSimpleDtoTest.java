package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingSimpleDtoTest {

    @Test
    void mapToBookingSimpleDto() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(10);
        User booker = new User();
        booker.setId(1);
        Item item = new Item();
        item.setId(1);
        Booking booking = Booking.builder().id(1).start(start).end(end).booker(booker)
                .item(item).status(BookingState.WAITING).build();
        BookingSimpleDto resultSimpleDto = BookingSimpleDto.mapToBookingSimpleDto(booking);
        assertEquals(booking.getId(), resultSimpleDto.getId());
        assertEquals(booking.getEnd(), resultSimpleDto.getEnd());
        assertEquals(booking.getStart(), resultSimpleDto.getStart());
        assertEquals(booking.getItem().getId(), resultSimpleDto.getItemId());
        assertEquals(booking.getBooker().getId(), resultSimpleDto.getBookerId());
        assertEquals(booking.getStatus(), resultSimpleDto.getStatus());
    }
}