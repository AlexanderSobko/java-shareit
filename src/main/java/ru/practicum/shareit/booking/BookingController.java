package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public ResponseEntity<BookingDto> saveBooking(@RequestBody @Valid BookingCreationDto dto,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.status(201).body(service.saveBooking(dto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBookingStatusByOwner(@PathVariable("bookingId") long bookingId,
                                                              @RequestHeader("X-Sharer-User-Id") long ownerId,
                                                              @RequestParam("approved") boolean isApproved) {
        return ResponseEntity.ok(service.updateBookingStatus(bookingId, ownerId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable("bookingId") long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(BookingDto.mapToBookingDto(service.getBooking(bookingId, userId)));
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return ResponseEntity.ok(service.getBookings(userId, state, false));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return ResponseEntity.ok(service.getBookings(userId, state, true));
    }

}
