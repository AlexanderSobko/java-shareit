package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreationDto {

    @NotBlank(message = "Имя не может быть пустым!")
    String name;
    @NotBlank(message = "Описанее не может быть пустым!")
    String description;
    @NotNull(message = "Информация о наличии предмета не может быть пустой!")
    Boolean available;

}
