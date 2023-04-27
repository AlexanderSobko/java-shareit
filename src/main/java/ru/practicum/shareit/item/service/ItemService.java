package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> searchItems(String text);

    List<ItemDto> getItems(Integer userId);

    ItemDto getItem(Integer itemId);

    ItemDto updateItem(ItemDto dto);

    ItemDto saveItem(ItemDto dto);

}
