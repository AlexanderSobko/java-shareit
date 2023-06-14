package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items", produces = "application/hal+json;charset=utf8")
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> saveItem(@RequestBody @Valid ItemCreationDto dto,
                                            @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return ResponseEntity.status(201).body(service.saveItem(dto, ownerId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemCreationDto dto,
                                              @PathVariable("id") long id,
                                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return ResponseEntity.ok(service.updateItem(dto, id, ownerId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable("itemId") long itemId,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(ItemDto.mapToItemDtoWithComments(service.getItem(itemId, userId)));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.getItems(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam("text") String text,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.searchItems(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> saveComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable("itemId") long itemId,
                                                  @RequestBody @Valid CommentCreationDto dto) {
        return ResponseEntity.ok(service.saveComment(userId, itemId, dto));
    }

}
