package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserCreationDto userCreationDto);

    UserDto updateUser(UserCreationDto userCreationDto);

    User getUser(Long id);

    String deleteUser(Long id);

    List<UserDto> getAll();
}
