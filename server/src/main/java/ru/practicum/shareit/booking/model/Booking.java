package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "start_date")
    LocalDateTime start;
    @Column(name = "end_date")
    LocalDateTime end;
    @JoinColumn(name = "user_id", updatable = false)
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    User booker;
    @JoinColumn(name = "item_id", updatable = false)
    @ManyToOne(targetEntity = Item.class, fetch = FetchType.EAGER)
    Item item;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "booking_state", nullable = false)
    BookingState status;

    public static Booking mapToBooking(BookingSimpleDto bookingCreationDto) {
        return Booking.builder()
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .status(BookingState.WAITING)
                .build();
    }

}
