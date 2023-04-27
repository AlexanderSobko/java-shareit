package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<ItemDto> saveItem(@RequestBody @Valid ItemDto dto,
                                            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        dto.setOwner(userId);
        return ResponseEntity.status(201).body(service.saveItem(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(@RequestBody ItemDto dto,
                                              @PathVariable("id") int id,
                                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        dto.setOwner(userId);
        dto.setId(id);
        return ResponseEntity.ok(service.updateItem(dto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable("itemId") Integer itemId) {
        return ResponseEntity.ok(service.getItem(itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ResponseEntity.ok(service.getItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam("text") String text) {
        return ResponseEntity.ok(service.searchItems(text));
    }

}
