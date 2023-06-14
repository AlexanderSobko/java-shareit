package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ResponseEntity<ItemRequestDto> saveItemRequest(@RequestBody @Valid ItemRequestCreationDto dto,
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
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.getAllStartingFrom(from, size, userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequest(@PathVariable("requestId") long itemRequestId,
                                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return ResponseEntity.ok(ItemRequestDto.mapToItemRequestDto(service.getById(itemRequestId, userId)));
    }

}
