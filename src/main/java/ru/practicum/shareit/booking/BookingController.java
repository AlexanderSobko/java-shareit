package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public ResponseEntity<Booking> saveBooking(@RequestBody @Valid BookingCreationDto dto,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.status(201).body(service.saveBooking(dto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Booking> updateBookingStatusByOwner(@PathVariable("bookingId") long bookingId,
                                                              @RequestHeader("X-Sharer-User-Id") long ownerId,
                                                              @RequestParam("approved") boolean isApproved) {
        return ResponseEntity.ok(service.updateBookingStatus(bookingId, ownerId, isApproved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBooking(@PathVariable("bookingId") long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(service.getBooking(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return ResponseEntity.ok(service.getBookings(userId, state, false));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Booking>> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return ResponseEntity.ok(service.getBookings(userId, state, true));
    }

}
