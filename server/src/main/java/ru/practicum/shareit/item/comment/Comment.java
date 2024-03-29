package ru.practicum.shareit.item.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String text;
    @JsonIgnore
    @JoinColumn(name = "user_id", updatable = false)
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    User user;
    @JsonIgnore
    @JoinColumn(name = "item_id", updatable = false)
    @ManyToOne(targetEntity = Item.class, fetch = FetchType.LAZY)
    Item item;
    String authorName;
    LocalDateTime created;

    public String toString() {
        return "Comment(id=" + this.getId() +
                ", text=" + this.getText() +
                ", user=" + this.getUser() +
                ", item=" + "Item(id=" + this.getId() +
                ", name=" + item.getName() +
                ", description=" + item.getDescription() +
                ", owner=" + item.getOwner() +
                ", authorName=" + this.getAuthorName() +
                ", created=" + this.getCreated() + ")";
    }
}
