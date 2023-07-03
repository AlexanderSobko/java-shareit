package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {

    long id;
    String description;
    User user;
    LocalDateTime created;
    List<ItemDto> items;

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .user(itemRequest.getUser())
                .created(itemRequest.getCreated())
                .items(itemRequest.getItems() != null
                        ? itemRequest.getItems().stream().map(ItemDto::mapToItemDto).collect(Collectors.toList())
                        : List.of())
                .build();
    }
}
