package ru.practicum.shareit.booking;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {

    BookingRepository bookingRepo;
    ItemService itemService;
    UserService userService;

    public BookingDto saveBooking(BookingCreationDto dto, long bookerId) {
        Booking booking = Booking.mapToBooking(dto);
        Item item = itemService.getItem(dto.getItemId(), bookerId);
        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Пользователь с id(%d) являтся владелцем вещи!");
        }
        booking.setItem(item);
        validateItem(item);
        validateTime(booking);
        User booker = userService.getUser(bookerId);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);
        booking = bookingRepo.save(booking);
        log.info("Бронь успешно создана! {}", booking);
        return BookingDto.mapToBookingDto(booking);
    }

    @Transactional
    public BookingDto updateBookingStatus(long bookingId, long ownerId, boolean isApproved) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с id(%d) не найдена!", bookingId)));
        if (booking.getStatus() == BookingState.APPROVED || booking.getStatus() == BookingState.REJECTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Статус заявки уже был изменен!");
        }
        if (ownerId != booking.getItem().getOwner().getId()) {
            throw new NotFoundException(
                    String.format("Пользователь с id(%d) не является владельцем!", ownerId));
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Бронь с id(%d) уже началас!", bookingId));
        }
        validateTime(booking);
        BookingState bookingState = isApproved ? BookingState.APPROVED : BookingState.REJECTED;
        booking.setStatus(bookingState);
        bookingRepo.save(booking);
        String message = String.format("Статус брони с id(%d) успешно изменен на \"%s\"!", bookingId, bookingState.rus);
        log.info(message);
        return BookingDto.mapToBookingDto(booking);
    }

    public Booking getBooking(long bookingId, long userId) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Бронь с id(%d) не найдена!", bookingId)));
        if (userId != booking.getBooker().getId() && userId != booking.getItem().getOwner().getId()) {
            throw new NotFoundException(
                    String.format("Пользователь с id(%d) не является автором бронирования," +
                            " либо владельцем вещи!", userId));
        }
        return booking;
    }

    public List<BookingDto> getBookings(long userId, String state, boolean isOwner) {
        userService.getUser(userId);
        BookingDtoState status = BookingDtoState.valueOf(state);
        Specification<Booking> specification = predicate(status, userId, isOwner);
        Sort sortByDateAsc = Sort.by(Sort.Order.desc("start"));
        return bookingRepo.findAll(specification, sortByDateAsc).stream()
                .map(BookingDto::mapToBookingDto)
                .collect(Collectors.toList());
    }

    private void validateItem(Item item) {
        if (!item.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Вещь с id(%d) не доступена!");
        }
    }

    private Specification<Booking> predicate(BookingDtoState state, long userId, boolean isOwner) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (isOwner) {
                Join<Booking, Item> bookingsItems = root.join("item");
                Predicate byOwnerId = cb.equal(bookingsItems.get("owner"), userId);
                predicates.add(byOwnerId);
            } else {
                Predicate byUserId = cb.equal(root.get("booker"), userId);
                predicates.add(byUserId);
            }
            switch (state) {
                case ALL:
                    break;
                case CURRENT:
                    Predicate betweenRentFromAndTo = cb.between(cb.currentTimestamp(), root.get("start"), root.get("end"));
                    predicates.add(betweenRentFromAndTo);
                    break;
                case PAST:
                    Predicate afterRentTo = cb.lessThanOrEqualTo(root.get("end"), cb.currentTimestamp());
                    predicates.add(afterRentTo);
                    break;
                case FUTURE:
                    Predicate beforeRentFrom = cb.greaterThan(root.get("start"), cb.currentDate());
                    predicates.add(beforeRentFrom);
                    break;
                case WAITING:
                    Predicate waitingState = cb.equal(root.get("status"), BookingState.WAITING);
                    predicates.add(waitingState);
                    break;
                case REJECTED:
                    Predicate rejectedState = cb.equal(root.get("status"), BookingState.REJECTED);
                    predicates.add(rejectedState);
                    break;
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void validateTime(Booking booking) {
        Specification<Booking> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate startBetween = cb.between(root.get("start"), booking.getStart(), booking.getEnd());
            Predicate endBetween = cb.between(root.get("end"), booking.getStart(), booking.getEnd());
            predicates.add(cb.or(startBetween, endBetween));
            Predicate statusApproved = cb.equal(root.get("status"), BookingState.APPROVED);
            predicates.add(statusApproved);
            Predicate itemId = cb.equal(root.get("item"), booking.getItem());
            predicates.add(itemId);
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        if (bookingRepo.exists(specification)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Вещь с id(%d) уже забронирована на запрашиваемые даты!", booking.getItem().getId()));
        }
    }

}
