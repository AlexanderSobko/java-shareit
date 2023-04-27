package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByUserId(Integer userId);

    Optional<Item> findByIdAndUserId(int id, int userId);

    List<Item> findByNameLikeOrDescriptionLikeAllIgnoreCaseAndAvailableTrue(String name, String description);

}
