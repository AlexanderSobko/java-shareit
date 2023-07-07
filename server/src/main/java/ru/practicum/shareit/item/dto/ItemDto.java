package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    long id;
    String name;
    String description;
    UserDto owner;
    int rentCount;
    Long requestId;
    Boolean available;
    BookingSimpleDto nextBooking;
    BookingSimpleDto lastBooking;
    List<CommentDto> comments;

    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserDto.mapToUserDto(item.getOwner()))
                .rentCount(item.getRentCount())
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .available(item.isAvailable())
                .lastBooking(item.getLastBooking())
                .nextBooking(item.getNextBooking())
                .build();
    }

    public static ItemDto mapToItemDtoWithComments(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserDto.mapToUserDto(item.getOwner()))
                .rentCount(item.getRentCount())
                .requestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null)
                .available(item.isAvailable())
                .lastBooking(item.getLastBooking())
                .nextBooking(item.getNextBooking())
                .comments(item.getComments() == null ?
                        List.of()
                        : item.getComments().stream().map(CommentDto::mapToCommentDto).collect(Collectors.toList()))
                .build();
    }
}
