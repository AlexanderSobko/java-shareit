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
    public UserDto saveUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        User result = userRepo.save(User.mapToUser(userDto));
        log.info("Данные пользователя с id({}) успешно сохранены. {}", result.getId(), result);
        return UserDto.mapToUserDto(result);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = userRepo.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id(%s) не найден!", userDto.getId())));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            validateEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        User result = userRepo.save(user);
        log.info("Данные пользователя с id({}) успешно обновлены. {}", result.getId(), result);
        return UserDto.mapToUserDto(result);
    }

    @Override
    public UserDto getUser(Integer id) {
        User result = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id(%s) не найден!", id)));
        return UserDto.mapToUserDto(result);
    }

    @Override
    public String deleteUser(Integer id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id(%s) не найден!", id)));
        userRepo.deleteById(id);
        String message = String.format("Данные о пользователе с id(%d) успешно удалены. %s", user.getId(), user);
        log.info(message);
        return message;
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
