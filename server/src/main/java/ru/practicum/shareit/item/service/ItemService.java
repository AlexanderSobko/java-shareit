package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> searchItems(String text, int from, int size);

    List<ItemDto> getItems(long userId, int from, int size);

    Item getItem(long itemId, long userId);

    ItemDto updateItem(ItemDto dto, long id, long ownerId);

    ItemDto saveItem(ItemDto dto, long ownerId);

    CommentDto saveComment(long userId, long itemId, CommentDto comment);
}
