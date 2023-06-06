package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @PostMapping
    public ResponseEntity<ItemCreationDto> saveItem(@RequestBody @Valid ItemCreationDto dto,
                                                    @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return ResponseEntity.status(201).body(service.saveItem(dto, ownerId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemCreationDto> updateItem(@RequestBody ItemCreationDto dto,
                                                      @PathVariable("id") long id,
                                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        dto.setOwner(userId);
        dto.setId(id);
        return ResponseEntity.ok(service.updateItem(dto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItem(@PathVariable("itemId") long itemId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(service.getItem(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<Item>> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(service.getItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemCreationDto>> searchItems(@RequestParam("text") String text) {
        return ResponseEntity.ok(service.searchItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Comment> saveComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable("itemId") long itemId,
                                               @RequestBody @Valid Comment comment) {
        return ResponseEntity.ok(service.saveComment(userId, itemId, comment));
    }

}
