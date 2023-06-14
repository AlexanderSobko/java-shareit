package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private final EasyRandom generator = new EasyRandom();
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(bookingRepository, itemService, userService);
    }

    @Test
    void failSaveBooking() {
        long bookerId = 101;
        BookingCreationDto bookingCreationDto = BookingCreationDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .itemId(101)
                .userId(bookerId)
                .build();
        Item item = Item.builder()
                .id(1)
                .name("sdvdsv")
                .available(false)
                .owner(new User(bookerId, "John", "john@gail.cf"))
                .build();

        when(itemService.getItem(anyLong(), anyLong())).thenReturn(item);
        String message = assertThrows(NotFoundException.class,
                () -> bookingService.saveBooking(bookingCreationDto, bookerId)).getMessage();
        assertEquals(String.format("Пользователь с id(%d) являтся владелцем вещи!", bookerId), message);

        message = assertThrows(ResponseStatusException.class,
                () -> bookingService.saveBooking(bookingCreationDto, 102)).getReason();
        assertEquals(String.format("Вещь с id(%d) не доступна!", item.getId()), message);

        item.setAvailable(true);
        when(bookingRepository.exists(any(Specification.class))).thenReturn(true);
        message = assertThrows(ResponseStatusException.class,
                () -> bookingService.saveBooking(bookingCreationDto, 102)).getReason();
        assertEquals("Вещь с id(1) уже забронирована на запрашиваемые даты!", message, "Вещь уже забронирована на запрашиваемые даты!");
    }

    @Test
    void updateBookingStatus() {
        Booking booking = generator.nextObject(Booking.class);
        booking.setStatus(BookingState.APPROVED);
        long bookingId = booking.getId();
        long ownerId = 101L;
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        String message = assertThrows(ResponseStatusException.class, () ->
                bookingService.updateBookingStatus(bookingId, ownerId, true)).getReason();
        assertEquals("Статус заявки уже был изменен!", message);

        booking.setStatus(BookingState.REJECTED);
        message = assertThrows(ResponseStatusException.class, () ->
                bookingService.updateBookingStatus(bookingId, ownerId, true)).getReason();
        assertEquals("Статус заявки уже был изменен!", message);

        booking.setStatus(BookingState.WAITING);
        message = assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(bookingId, ownerId, true)).getMessage();
        assertEquals(String.format("Пользователь с id(%d) не является владельцем!", ownerId), message);

        long ownerId1 = booking.getItem().getOwner().getId();
        booking.setStart(LocalDateTime.now().minusDays(1));
        message = assertThrows(ResponseStatusException.class, () ->
                bookingService.updateBookingStatus(bookingId, ownerId1, true)).getReason();
        assertEquals(String.format("Бронь с id(%d) уже началась!", bookingId), message);

        when(bookingRepository.exists(any(Specification.class)))
                .thenReturn(true);
        booking.setStart(LocalDateTime.now().plusDays(1));
        message = assertThrows(ResponseStatusException.class, () ->
                bookingService.updateBookingStatus(bookingId, ownerId1, true)).getReason();
        assertEquals(String.format("Вещь с id(%d) уже забронирована на запрашиваемые даты!", booking.getItem().getId()),
                message);
    }

    @Test
    void getBooking() {
        Item item = Item.builder()
                .id(1)
                .name("sdvdsv")
                .available(false)
                .owner(new User(101, "John", "john@gail.cf"))
                .build();
        Booking booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10))
                .item(item)
                .booker(generator.nextObject(User.class))
                .build();
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));
        String message = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(booking.getId(), 999)).getMessage();
        assertEquals("Пользователь с id(999) не является автором бронирования, либо владельцем вещи!", message);
    }
}