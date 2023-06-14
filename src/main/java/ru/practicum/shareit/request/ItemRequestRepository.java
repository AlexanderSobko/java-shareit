package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query(value = "Select ir From ItemRequest ir " +
            "Left Join Fetch ir.items items " +
            "Join Fetch ir.user user " +
            "Where ir.user = :user")
    List<ItemRequest> findAllByUser(@Param("user") User user);

    @Query(value = "Select ir From ItemRequest ir " +
            "Left Join Fetch ir.items items " +
            "Join Fetch ir.user user " +
            "Where ir.user != :user")
    List<ItemRequest> findAllByUserNot(User user, Pageable pageable);
}
