package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    String description;
    @JoinColumn(name = "owner_id")
    @ManyToOne(targetEntity = User.class)
    User owner;
    int rentCount;
    boolean available;
    @Transient
    BookingDto nextBooking;
    @Transient
    BookingDto lastBooking;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item", cascade = CascadeType.REMOVE)
    List<Comment> comments;


    public static Item mapToItem(ItemCreationDto itemCreationDto) {
        return Item.builder()
                .id(itemCreationDto.getId())
                .name(itemCreationDto.getName())
                .description(itemCreationDto.getDescription())
                .available(itemCreationDto.getAvailable())
                .build();
    }

}
