package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items", produces = "application/hal+json;charset=utf8")
public class ItemController {

    private final ItemAndCommentClient itemAndCommentClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestBody @Valid ItemCreationDto dto,
                                            @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemAndCommentClient.saveItem(dto, ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemCreationDto dto,
                                              @PathVariable("id") long id,
                                              @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return itemAndCommentClient.updateItem(dto, id, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object>  getItem(@PathVariable("itemId") long itemId,
                                           @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemAndCommentClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return itemAndCommentClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam("text") String text,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return itemAndCommentClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable("itemId") long itemId,
                                                  @RequestBody @Valid CommentCreationDto dto) {
        return itemAndCommentClient.saveComment(userId, itemId, dto);
    }

}
