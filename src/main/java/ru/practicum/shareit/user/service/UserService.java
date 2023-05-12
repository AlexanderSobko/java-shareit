package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    UserDto getUser(Integer id);

    String deleteUser(Integer id);

    List<UserDto> getAll();
}
