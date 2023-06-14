package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImpTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestService itemRequestService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    private ItemServiceImp itemService;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImp(
                itemRepository, userService, itemRequestService, bookingRepository, commentRepository
        );
    }

    @Test
    void searchItems() {
    }

    @Test
    void getItems() {
    }

    @Test
    void getItem() {
    }

    @Test
    void updateItem() {
    }

    @Test
    void saveItem() {
    }

    @Test
    void saveComment() {
        assertThrows(ResponseStatusException.class,
                () -> itemService.saveComment(999, 999, new CommentCreationDto()));
    }

    @Test
    void setNextAndLastBookings() {
    }
}