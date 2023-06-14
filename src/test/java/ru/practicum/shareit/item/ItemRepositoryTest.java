package ru.practicum.shareit.item;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql(scripts = "file:src/test/resources/sql/init-data.sql")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/findByOwnerItemRepo.csv", delimiter = '|')
    void findByOwner(long ownerId, int pageSize, int resultSize) {
        List<Item> result = itemRepository.findByOwner(ownerId, PageRequest.of(0, pageSize));
        assertEquals(resultSize, result.size());
        result.forEach(item -> {
            assertEquals(ownerId, item.getOwner().getId());
        });
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/findByIdAndOwnerIdItemRepo.csv", delimiter = '|')
    void findByIdAndOwnerId(long id, long ownerId, boolean isResultPresent) {
        Optional<Item> item = itemRepository.findByIdAndOwnerId(id, ownerId);
        if (isResultPresent) {
            assertTrue(item.isPresent());
            assertEquals(id, item.get().getId());
            assertEquals(ownerId, item.get().getOwner().getId());
        } else {
            assertFalse(item.isPresent());
        }
    }
}