package ru.practicum.shareit.booking.dto;

import java.util.stream.Stream;


public enum BookingDtoState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public boolean contains(String state) {
        return Stream.of(BookingDtoState.values()).map(BookingDtoState::name).toList().contains(state);
    }
}
