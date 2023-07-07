package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepo;

    @Override
    public UserDto saveUser(UserDto dto) {
//        validateEmail(dto.getEmail());
        User result = userRepo.save(User.mapToUser(dto));
        log.info("Данные пользователя с id({}) успешно сохранены. {}", result.getId(), result);
        return UserDto.mapToUserDto(result);
    }

    @Override
    public UserDto updateUser(UserDto dto) {
        User user = getUser(dto.getId());
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            validateEmail(dto.getEmail());
            user.setEmail(dto.getEmail());
        }
        User result = userRepo.save(user);
        log.info("Данные пользователя с id({}) успешно обновлены. {}", result.getId(), result);
        return UserDto.mapToUserDto(result);
    }

    @Override
    public User getUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id(%s) не найден!", id)));
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id(%s) не найден!", id)));
        userRepo.deleteById(id);
        log.info(String.format("Данные о пользователе с id(%d) успешно удалены. %s", user.getId(), user));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepo.findAll().stream()
                .map(UserDto::mapToUserDto)
                .collect(Collectors.toList());
    }

    private void validateEmail(String email) {
        if (userRepo.existsByEmail(email)) {
            throw new AlreadyExistsException("Пользователь с такой почтой уже зарегестрирован!");
        }
    }

}
