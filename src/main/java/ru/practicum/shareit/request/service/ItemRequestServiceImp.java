package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepo;
    private final UserService userService;

    @Override
    public ItemRequestDto save(ItemRequestCreationDto dto, long userId) {
        ItemRequest itemRequest = new ItemRequest(dto.getDescription());
        itemRequest.setUser(userService.getUser(userId));
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestDto.mapToItemRequestDto(itemRequestRepo.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllByUserId(long userId) {
        return itemRequestRepo.findAllByUser(userService.getUser(userId)).stream()
                .map(ItemRequestDto::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllStartingFrom(int from, int size, long userId) {
        Sort sortByDate = Sort.by(Sort.Order.desc("created"));
        User user = userService.getUser(userId);
        Pageable pageable = new PageRequest(0, size, sortByDate) {
            @Override
            public long getOffset() {
                return from;
            }
        };
        return itemRequestRepo.findAllByUserNot(user, pageable).stream()
                .map(ItemRequestDto::mapToItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequest getById(long itemRequestId, long userId) {
        userService.getUser(userId);
        return itemRequestRepo.findById(itemRequestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с таким id(%d) не существует!", itemRequestId)));
    }
}
