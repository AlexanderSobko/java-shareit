package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String description;
    @JoinColumn(name = "owner_id")
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    User owner;
    int rentCount;
    boolean available;
    @Transient
    BookingSimpleDto nextBooking;
    @Transient
    BookingSimpleDto lastBooking;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item", cascade = CascadeType.REMOVE)
    List<Comment> comments;


    public static Item mapToItem(ItemCreationDto itemCreationDto) {
        return Item.builder()
                .name(itemCreationDto.getName())
                .description(itemCreationDto.getDescription())
                .available(itemCreationDto.getAvailable())
                .build();
    }

}
