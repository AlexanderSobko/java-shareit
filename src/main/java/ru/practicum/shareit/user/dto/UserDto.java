package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {

    int id;
    @NotBlank(message = "Имя пользователя не может быть пустым!")
    String name;
    @Email(regexp = "\\w+@\\w+\\.\\w+", message = "Электронная почта должна быть валидной!")
    @NotBlank(message = "Электронная почта не может быть пустой!")
    String email;

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
