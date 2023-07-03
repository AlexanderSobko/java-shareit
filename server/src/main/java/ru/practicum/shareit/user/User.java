package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    @Column(unique = true)
    String email;

    public static User mapToUser(UserDto dto) {
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }

}
