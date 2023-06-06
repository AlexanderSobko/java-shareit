package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreationDto;

import java.util.List;

public interface UserService {

    UserCreationDto saveUser(UserCreationDto userCreationDto);

    User updateUser(UserCreationDto userCreationDto);

    User getUser(Long id);

    String deleteUser(Long id);

    List<UserCreationDto> getAll();
}
