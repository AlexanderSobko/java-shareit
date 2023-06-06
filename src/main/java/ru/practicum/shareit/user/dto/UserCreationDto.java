package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@ToString
public class UserCreationDto {

    long id;
    @NotBlank(message = "Имя пользователя не может быть пустым!")
    String name;
    @Email(regexp = "\\w+@\\w+\\.\\w+", message = "Электронная почта должна быть валидной!")
    @NotBlank(message = "Электронная почта не может быть пустой!")
    String email;

    public static UserCreationDto mapToUserDto(User user) {
        return UserCreationDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}