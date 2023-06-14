package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserCreationDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImp(userRepository);
    }

    @Test
    void userAlreadyExistsUpdateUser() {
        UserCreationDto dto = UserCreationDto.builder()
                .id(1)
                .email("userUpdate@email.com")
                .build();
        User user = new User(1, "name", "user@email.com");
        when(userRepository.existsByEmail("userUpdate@email.com"))
                .thenReturn(true);
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        String exceptionMessage = assertThrows(AlreadyExistsException.class, () -> {
            userService.updateUser(dto);
        }).getMessage();
        assertEquals("Пользователь с такой почтой уже зарегестрирован!", exceptionMessage);
    }

    @Test
    void emailUpdateUser() {
        UserCreationDto dto = UserCreationDto.builder()
                .id(1)
                .email("userUpdate@email.com")
                .build();
        User user = new User(1, "name", "user@email.com");
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        user.setEmail(dto.getEmail());
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.updateUser(dto);
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(argumentCaptor.capture());
        assertEquals(dto.getEmail(), argumentCaptor.getValue().getEmail());
    }

    @Test
    void nameUpdateUser() {
        UserCreationDto dto = UserCreationDto.builder()
                .id(1)
                .name("nameUpdate")
                .build();
        User user = new User(1, "name", "user@email.com");
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        user.setName(dto.getName());
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.updateUser(dto);
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(argumentCaptor.capture());
        assertEquals(dto.getName(), argumentCaptor.getValue().getName());
    }

    @Test
    void emptyDtoUpdateUser() {
        UserCreationDto dto = UserCreationDto.builder()
                .id(1)
                .build();
        User user = new User(1, "name", "user@email.com");
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
                .thenReturn(user);
        userService.updateUser(dto);
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(argumentCaptor.capture());
        assertEquals(user.getName(), argumentCaptor.getValue().getName());
        assertEquals(user.getEmail(), argumentCaptor.getValue().getEmail());
    }

}