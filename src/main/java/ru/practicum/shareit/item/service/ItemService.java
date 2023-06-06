package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemCreationDto;

import java.util.List;

public interface ItemService {

    List<ItemCreationDto> searchItems(String text);

    List<Item> getItems(long userId);

    Item getItem(long itemId, long userId);

    ItemCreationDto updateItem(ItemCreationDto dto);

    ItemCreationDto saveItem(ItemCreationDto dto, long ownerId);

    Comment saveComment(long userId, long itemId, Comment comment);
}
