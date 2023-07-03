package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreationDto {

    @NotBlank(message = "Комментарий не может быть пустым!")
    String text;

}
