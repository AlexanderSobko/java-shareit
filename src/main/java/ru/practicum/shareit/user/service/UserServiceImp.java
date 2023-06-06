package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepo;

    @Override
    public UserCreationDto saveUser(UserCreationDto userCreationDto) {
//        validateEmail(userCreationDto.getEmail());
        User result = userRepo.save(User.mapToUser(userCreationDto));
        log.info("Данные пользователя с id({}) успешно сохранены. {}", result.getId(), result);
        return UserCreationDto.mapToUserDto(result);
    }

    @Override
    public User updateUser(UserCreationDto userCreationDto) {
        User user = getUser(userCreationDto.getId());
        if (userCreationDto.getName() != null) {
            user.setName(userCreationDto.getName());
        }
        if (userCreationDto.getEmail() != null && !userCreationDto.getEmail().equals(user.getEmail())) {
            validateEmail(userCreationDto.getEmail());
            user.setEmail(userCreationDto.getEmail());
        }
        User result = userRepo.save(user);
        log.info("Данные пользователя с id({}) успешно обновлены. {}", result.getId(), result);
        return result;
    }

    @Override
    public User getUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id(%s) не найден!", id)));
    }

    @Override
    public String deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id(%s) не найден!", id)));
        userRepo.deleteById(id);
        String message = String.format("Данные о пользователе с id(%d) успешно удалены. %s", user.getId(), user);
        log.info(message);
        return message;
    }

    @Override
    public List<UserCreationDto> getAll() {
        return userRepo.findAll().stream()
                .map(UserCreationDto::mapToUserDto)
                .collect(Collectors.toList());
    }

    private void validateEmail(String email) {
        if (userRepo.existsByEmail(email)) {
            throw new AlreadyExistsException("Пользователь с такой почтой уже зарегестрирован!");
        }
    }

}
