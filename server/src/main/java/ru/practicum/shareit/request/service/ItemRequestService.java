package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto save(ItemRequestDto dto, long userId);

    List<ItemRequestDto> getAllByUserId(long userId);

    List<ItemRequestDto> getAllStartingFrom(int from, int size, long userId);

    ItemRequest getById(long itemRequestId, long userId);

}
