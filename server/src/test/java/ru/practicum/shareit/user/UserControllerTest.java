package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    final String basePath = "/users";
    final EasyRandom generator = new EasyRandom();
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/users/successfullySaveUser.csv", delimiter = '|')
    public void successfullySaveUser(String fileJson, String expectedResponse) throws Exception {
        when(userService.saveUser(any()))
                .thenReturn(objectMapper.readValue(expectedResponse, UserDto.class));
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/users/successfullyUpdateUser.csv", delimiter = '|')
    void updateUser(long userId, String updateJson, String expectedResponseJson) throws Exception {
        when(userService.updateUser(any()))
                .thenReturn(objectMapper.readValue(expectedResponseJson, UserDto.class));
        mockMvc.perform(patch(basePath + "/" + userId)
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponseJson));
    }

    @Test
    void getUsers() throws Exception {
        List<UserDto> list = List.of(generator.nextObject(UserDto.class), generator.nextObject(UserDto.class));
        when(userService.getAll())
                .thenReturn(list);
        mockMvc.perform(get(basePath))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(list)));
    }

    @Test
    void successfullyDeleteUser() throws Exception {
        String path = basePath + "/101";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void failDeleteUser() throws Exception {
        doThrow(new NotFoundException("Пользователь с id(999) не найден!")).when(userService).deleteUser(999L);
        mockMvc.perform(delete(basePath + "/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Пользователь с id(999) не найден!")));
    }

}