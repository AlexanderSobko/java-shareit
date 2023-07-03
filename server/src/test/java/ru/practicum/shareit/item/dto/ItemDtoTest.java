package ru.practicum.shareit.item.dto;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDtoTest {

    private final EasyRandom generator = new EasyRandom();

    @Test
    void mapToItemDtoWithComments() {
        Item item = generator.nextObject(Item.class);
        item.setComments(null);
        ItemDto itemDto = ItemDto.mapToItemDtoWithComments(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getItemRequest().getId(), itemDto.getRequestId());
        assertEquals(item.getLastBooking(), itemDto.getLastBooking());
        assertEquals(item.getNextBooking(), itemDto.getNextBooking());
        assertEquals(List.of(), itemDto.getComments());
    }
}