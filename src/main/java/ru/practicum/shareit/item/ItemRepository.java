package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, JpaSpecificationExecutor<Item> {

    @Query("Select i from Item i left join fetch i.comments comments where i.owner.id = :ownerId")
    List<Item> findByOwner(@Param("ownerId") long ownerId, Pageable pageable);

    Optional<Item> findByIdAndOwnerId(long id, long ownerId);

}
