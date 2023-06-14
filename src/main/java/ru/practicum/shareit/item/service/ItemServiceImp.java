package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImp implements ItemService {

    ItemRepository itemRepo;
    UserService userService;
    ItemRequestService itemRequestService;
    BookingRepository bookingRepo;
    CommentRepository commentRepo;

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        }
        Pageable pageable = new PageRequest(0, size, Sort.unsorted()) {
            @Override
            public long getOffset() {
                return from;
            }
        };
        text = "%" + text + "%";
        Page<Item> items = itemRepo.findAll(withNameOrDescriptionSource(text), pageable);
        return items.stream()
                .map(ItemDto::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItems(long userId, int from, int size) {
        Pageable pageable = new PageRequest(0, size, Sort.unsorted()) {
            @Override
            public long getOffset() {
                return from;
            }
        };
        List<Item> items = itemRepo.findByOwner(userId, pageable);
        if (!items.isEmpty()) {
            setNextAndLastBookings(items);
            return items.stream()
                    .map(ItemDto::mapToItemDtoWithComments)
                    .collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    @Override
    public Item getItem(long itemId, long userId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id(%s) не найдена!", itemId)));
        if (userId == item.getOwner().getId()) {
            setNextAndLastBookings(List.of(item));
        }
        return item;
    }

    @Override
    public ItemDto updateItem(ItemCreationDto dto, long id, long ownerId) {
        Item item = itemRepo.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Вещь с id(%d) у пользоватедя с id(%d) не найдена!", id, ownerId)));
        if (dto.getName() != null
                && !dto.getName().isBlank()
                && !item.getName().equals(dto.getName())) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null
                && !dto.getDescription().isBlank()
                && !item.getDescription().equals(dto.getDescription())) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null && item.isAvailable() != dto.getAvailable()) {
            item.setAvailable(dto.getAvailable());
        }
        if (dto.getRequestId() != null
                && (item.getItemRequest() == null || dto.getRequestId() != item.getItemRequest().getId())) {
            ItemRequest itemRequest = itemRequestService.getById(dto.getRequestId(), ownerId);
            item.setItemRequest(itemRequest);
        }
        ItemDto result = ItemDto.mapToItemDto(itemRepo.save(item));
        log.info("Данные предмета с id({}) успешно обновлены. {}", item.getId(), dto);
        return result;
    }

    @Override
    public ItemDto saveItem(ItemCreationDto dto, long ownerId) {
        User owner = userService.getUser(ownerId);
        Item item = Item.mapToItem(dto);
        if (dto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestService.getById(dto.getRequestId(), ownerId);
            item.setItemRequest(itemRequest);
        }
        item.setOwner(owner);
        item.setAvailable(true);
        item = itemRepo.save(item);
        log.info("Предмет с id({}) успешно сохранен. {}", item.getId(), item);
        return ItemDto.mapToItemDto(item);
    }

    @Override
    public CommentDto saveComment(long userId, long itemId, CommentCreationDto dto) {
        if (!bookingRepo.exists(withBookingCompleteByUserSource(userId, itemId))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Пользователь с id(%d) не брал вещь в аренду!", userId));
        }
        Comment comment = new Comment();
        User user = userService.getUser(userId);
        Item item = getItem(itemId, userId);
        comment.setItem(item);
        comment.setUser(user);
        comment.setText(dto.getText());
        comment.setAuthorName(user.getName());
        comment.setCreated(LocalDateTime.now());
        comment = commentRepo.save(comment);
        log.info("Комментарий к вещи с id({}) успешно сохранен. {}", item.getId(), comment);
        return CommentDto.mapToCommentDto(comment);
    }

    private Specification<Booking> withBookingCompleteByUserSource(long userId, long itemId) {
        return (root, query, cb) -> {
            Predicate byBookerId = cb.equal(cb.upper(root.get("booker")), userId);
            Predicate byItemId = cb.equal(cb.upper(root.get("item")), itemId);
            Predicate byState = cb.equal(root.get("status"), BookingState.APPROVED);
            Predicate byStart = cb.lessThan(root.get("end"), cb.currentTimestamp());
            return cb.and(byBookerId, byState, byStart, byItemId);
        };
    }

    private Specification<Item> withNameOrDescriptionSource(String source) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Predicate byName = cb.like(cb.upper(root.get("name")), source.toUpperCase());
            Predicate byDescription = cb.like(cb.upper(root.get("description")), source.toUpperCase());
            predicates.add(cb.or(byName, byDescription));
            Predicate byAvailable = cb.isTrue(root.get("available"));
            predicates.add(byAvailable);
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public void setNextAndLastBookings(List<Item> items) {
        Sort sortByDate = Sort.by(Sort.Order.asc("start"));
        Map<Long, BookingSimpleDto> nextBookings = bookingRepo.findAll(predicateForLastOrNext(items, true), sortByDate)
                .stream().collect(
                        Collectors.groupingBy(
                                booking -> booking.getItem().getId(),
                                HashMap::new,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        v -> BookingSimpleDto.mapToBookingSimpleDto(v.get(0)))));
        sortByDate = Sort.by(Sort.Order.desc("end"));
        Map<Long, BookingSimpleDto> lastBookings = bookingRepo.findAll(predicateForLastOrNext(items, false), sortByDate)
                .stream().collect(
                        Collectors.groupingBy(
                                booking -> booking.getItem().getId(),
                                HashMap::new,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        v -> BookingSimpleDto.mapToBookingSimpleDto(v.get(0)))));
        items.forEach(item -> {
            item.setLastBooking(lastBookings.getOrDefault(item.getId(), null));
            item.setNextBooking(nextBookings.getOrDefault(item.getId(), null));
        });
    }

    private Specification<Booking> predicateForLastOrNext(List<Item> items, boolean isNext) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Path<Item> item = root.get("item");
            Predicate byItemId = item.in(items);
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
