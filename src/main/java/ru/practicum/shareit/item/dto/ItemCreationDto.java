package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCreationDto {

    long id;
    @NotBlank(message = "Имя не может быть пустым!")
    String name;
    @NotBlank(message = "Описанее не может быть пустым!")
    String description;
    long owner;
    int rentCount;
    @NotNull(message = "Информация о наличии предмета не может быть пустой!")
    Boolean available;


    public static ItemCreationDto mapToItemDto(Item item) {
        return ItemCreationDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

}
