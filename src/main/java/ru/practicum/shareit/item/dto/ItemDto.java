package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    int id;
    @NotBlank(message = "Имя не может быть пустым!")
    String name;
    @NotBlank(message = "Описанее не может быть пустым!")
    String description;
    int owner;
    int rentCount;
    @NotNull(message = "Информация о наличии предмета не может быть пустой!")
    Boolean available;


    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getUserId())
                .available(item.isAvailable())
                .build();
    }

}
