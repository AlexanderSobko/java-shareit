package ru.practicum.shareit.booking.model;

public enum BookingState {
    APPROVED("Подтвержден"),
    REJECTED("Отклонен"),
    WAITING("Ожидает");

    public final String rus;

    BookingState(String rus) {
        this.rus = rus;
    }
}
