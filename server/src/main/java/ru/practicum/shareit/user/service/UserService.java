package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto dto);

    UserDto updateUser(UserDto dto);

    User getUser(Long id);

    void deleteUser(Long id);

    List<UserDto> getAll();
}
