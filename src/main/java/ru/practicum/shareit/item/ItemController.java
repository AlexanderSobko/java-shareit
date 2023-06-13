package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreationDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
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
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(service.getItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        return ResponseEntity.ok(service.searchItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> saveComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable("itemId") long itemId,
                                                  @RequestBody @Valid CommentCreationDto dto) {
        return ResponseEntity.ok(service.saveComment(userId, itemId, dto));
    }

}
