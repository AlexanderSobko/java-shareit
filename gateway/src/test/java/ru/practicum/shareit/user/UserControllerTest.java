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
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserCreationDto;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
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
    UserClient userClient;
    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/users/successfullySaveUser.csv", delimiter = '|')
    public void successfullySaveUser(String fileJson, String expectedResponse) throws Exception {
        when(userClient.saveUser(any()))
                .thenReturn(ResponseEntity.status(201).body(expectedResponse));
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/users/failSaveUser.csv", delimiter = '|')
    public void failSaveUser(String fileJson, String expectedResponse) throws Exception {
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/users/successfullyUpdateUser.csv", delimiter = '|')
    void updateUser(long userId, String updateJson, String expectedResponseJson) throws Exception {
        when(userClient.updateUser(any()))
                .thenReturn(ResponseEntity.ok(expectedResponseJson));
        mockMvc.perform(patch(basePath + "/" + userId)
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponseJson));
    }

    @Test
    void getUsers() throws Exception {
        List<UserCreationDto> list = List.of(generator.nextObject(UserCreationDto.class), generator.nextObject(UserCreationDto.class));
        when(userClient.getAll())
                .thenReturn(ResponseEntity.ok(list));
        mockMvc.perform(get(basePath))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(objectMapper.writeValueAsString(list))));
    }

    @Test
    void successfullyDeleteUser() throws Exception {
        String path = basePath + "/101";
        mockMvc.perform(delete(path))
                .andDo(print())
                .andExpect(status().isOk());
    }

}