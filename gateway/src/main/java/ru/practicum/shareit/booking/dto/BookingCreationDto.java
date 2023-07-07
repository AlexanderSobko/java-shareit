package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingCreationDto {

    @NotNull(message = "Время начала бронирования не может быть пустым!")
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом!")
    LocalDateTime start;
    @NotNull(message = "Время начала бронирования не может быть пустым!")
    @FutureOrPresent(message = "Время окончания бронирования не может быть в прошлом!")
    LocalDateTime end;
    long userId;
    @Min(value = 1, message = "Id вещи не может быть отрицательным!")
    long itemId;

    @AssertTrue(message = "Время окончания бронирования не может быть раньше или равно времени начала бронирования!")
    public boolean isValidRange() {
        if (start != null && end != null) {
            return start.isBefore(end) && !start.equals(end);
        } else {
            return true;
        }
    }
}
