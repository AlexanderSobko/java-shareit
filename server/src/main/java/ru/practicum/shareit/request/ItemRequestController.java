package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ResponseEntity<ItemRequestDto> saveItemRequest(@RequestBody ItemRequestDto dto,
                                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.status(201).body(service.save(dto, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getItemRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(service.getAllByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getItemRequestsStartingFrom(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getAllStartingFrom(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequest(@PathVariable("requestId") long itemRequestId,
                                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(ItemRequestDto.mapToItemRequestDto(service.getById(itemRequestId, userId)));
    }

}
