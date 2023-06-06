package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImp implements ItemService {

    ItemRepository itemRepo;
    UserService userService;
    BookingRepository bookingRepo;
    CommentRepository commentRepo;

    @Override
    public List<ItemCreationDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        text = "%" + text + "%";
        List<Item> items = itemRepo.findAll(withNameOrDescriptionSource(text));
        return items.stream().map(ItemCreationDto::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public List<Item> getItems(long userId) {
        List<Item> items = itemRepo.findByOwnerId(userId);
        items.forEach(item -> {
            item.setLastBooking(getLastBooking(item.getId()));
            item.setNextBooking(getNextBooking(item.getId()));
        });
        return items;
    }

    @Override
    public Item getItem(long itemId, long userId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id(%s) не найдена!", itemId)));
        if (userId == item.getOwner().getId()) {
            item.setNextBooking(getNextBooking(itemId));
            item.setLastBooking(getLastBooking(itemId));
        }
        return item;
    }

    @Override
    public ItemCreationDto updateItem(ItemCreationDto dto) {
        Item item = itemRepo.findByIdAndOwnerId(dto.getId(), dto.getOwner())
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Вещь с id(%d) у пользоватедя с id(%d) не найдена!", dto.getId(), dto.getOwner())));
        if (dto.getName() != null && !item.getName().equals(dto.getName())) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !item.getDescription().equals(dto.getDescription())) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null && item.isAvailable() != dto.getAvailable()) {
            item.setAvailable(dto.getAvailable());
        }
        item.setNextBooking(getNextBooking(item.getId()));
        item.setLastBooking(getLastBooking(item.getId()));
        ItemCreationDto result = ItemCreationDto.mapToItemDto(itemRepo.save(item));
        log.info("Данные предмета с id({}) успешно обновлены. {}", dto.getId(), dto);
        return result;
    }

    @Override
    public ItemCreationDto saveItem(ItemCreationDto dto, long ownerId) {
        User owner = userService.getUser(ownerId);
        Item item = Item.mapToItem(dto);
        item.setOwner(owner);
        item.setAvailable(true);
        item = itemRepo.save(item);
        log.info("Предмет с id({}) успешно сохранен. {}", dto.getId(), dto);
        return ItemCreationDto.mapToItemDto(item);
    }

    @Override
    public Comment saveComment(long userId, long itemId, Comment comment) {
        if (!bookingRepo.exists(withBookingCompleteByUserSource(userId, itemId))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Пользователь с id(%d) не брал вещь в аренду!", userId));
        }
        User user = userService.getUser(userId);
        Item item = getItem(itemId, userId);
        comment.setItem(item);
        comment.setUser(user);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthorName(user.getName());
        comment = commentRepo.save(comment);
        return comment;
    }

    private Specification<Booking> withBookingCompleteByUserSource(long userId, long itemId) {
        return (root, query, cb) -> {
            Predicate byBookerId = cb.equal(cb.upper(root.get("booker")), userId);
            Predicate byItemId = cb.equal(cb.upper(root.get("item")), itemId);
            Predicate byState = cb.equal(root.get("status"), BookingState.APPROVED);
            Predicate byStart = cb.lessThan(root.get("start"), cb.currentTimestamp());
            return cb.and(byBookerId, byState, byStart, byItemId);
        };
    }

    private Specification<Item> withNameOrDescriptionSource(String source) {
        return (root, query, cb) -> {
            Predicate byName = cb.like(cb.upper(root.get("name")), source.toUpperCase());
            Predicate byDescription = cb.like(cb.upper(root.get("description")), source.toUpperCase());
            Predicate byAvailable = cb.isTrue(root.get("available"));
            return cb.and(cb.or(byName, byDescription), byAvailable);
        };
    }

    public BookingDto getLastBooking(long itemId) {
        Sort sortByDate = Sort.by(Sort.Order.desc("end"));
        List<Booking> bookings = bookingRepo.findAll(predicateForLastOrNext(itemId, false), sortByDate);
        if (bookings.isEmpty()) {
            return null;
        } else {
            Booking booking = bookings.get(0);
            return BookingDto.mapToBookingDto(booking);
        }
    }

    public BookingDto getNextBooking(long itemId) {
        Sort sortByDate = Sort.by(Sort.Order.asc("start"));
        List<Booking> bookings = bookingRepo.findAll(predicateForLastOrNext(itemId, true), sortByDate);
        if (bookings.isEmpty()) {
            return null;
        } else {
            Booking booking = bookings.get(0);
            return BookingDto.mapToBookingDto(booking);
        }
    }

    private Specification<Booking> predicateForLastOrNext(long itemId, boolean isNext) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate byItemId = cb.equal(root.get("item"), itemId);
            predicates.add(byItemId);
            Predicate isApproved = cb.equal(root.get("status"), BookingState.APPROVED);
            predicates.add(isApproved);
            if (isNext) {
                Predicate rentStartAfter = cb.lessThanOrEqualTo(cb.currentTimestamp(), root.get("start"));
                predicates.add(rentStartAfter);
            } else {
                Predicate rentEndBefore = cb.greaterThan(cb.currentTimestamp(), root.get("start"));
                predicates.add(rentEndBefore);
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

}
