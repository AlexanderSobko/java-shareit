package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @NotBlank(message = "Комментарий не может быть пустым!")
    String text;
    @JsonIgnore
    @JoinColumn(name = "user_id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = User.class)
    User user;
    @JsonIgnore
    @JoinColumn(name = "item_id", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Item.class)
    Item item;
    String authorName;
    LocalDateTime created;

}
