package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(scripts = "file:src/test/resources/sql/init-data.sql")
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findAllByUser() {
        Optional<User> user = userRepository.findById(101L);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByUser(user.get());
        assertEquals(2, itemRequestList.size());
        assertEquals(user.get().getId(), itemRequestList.get(0).getUser().getId());
        assertEquals(user.get().getId(), itemRequestList.get(1).getUser().getId());
    }

    @Test
    void findAllByUserNot() {
        Optional<User> user = userRepository.findById(101L);
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByUserNot(user.get(), pageable);
        assertEquals(3, itemRequestList.size());
    }
}