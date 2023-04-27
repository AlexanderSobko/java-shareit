package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        text = "%" + text + "%";
        return itemRepo.findByNameLikeOrDescriptionLikeAllIgnoreCaseAndAvailableTrue(text, text).stream()
                .map(ItemDto::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
        return itemRepo.findByUserId(userId).stream().map(ItemDto::mapToItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id(%s) не найдена!", itemId)));
        return ItemDto.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto dto) {
        Item item = itemRepo.findByIdAndUserId(dto.getId(), dto.getOwner())
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Вещь с id(%d) у пользоватедя с id(%d) не найдена!", dto.getId(), dto.getOwner())));
        if (dto.getName() != null && !item.getName().equals(dto.getName())) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null && !item.getDescription().equals(dto.getDescription())) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null && item.isAvailable() != dto.getAvailable()) {
            item.setAvailable(dto.getAvailable());
        }
        ItemDto result = ItemDto.mapToItemDto(itemRepo.save(item));
        log.info("Данные предмета с id({}) успешно обновлены. {}", dto.getId(), dto);
        return result;
    }

    @Override
    public ItemDto saveItem(ItemDto dto) {
        validateUser(dto.getOwner());
        Item item = itemRepo.save(Item.mapToItem(dto));
        log.info("Предмет с id({}) успешно сохранен. {}", dto.getId(), dto);
        return ItemDto.mapToItemDto(item);
    }

    private void validateUser(int userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id(%s) не найден!", userId));
        }
    }

}
