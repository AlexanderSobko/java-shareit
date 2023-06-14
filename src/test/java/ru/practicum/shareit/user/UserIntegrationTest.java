package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integration-test.properties")
class UserIntegrationTest {

    private final String basePath = "/users";
    private final MockMvc mockMvc;

    @Autowired
    UserIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/users/successfullySaveUser.csv", delimiter = '|')
    public void successfullySaveUser(String fileJson, String expectedResponse) throws Exception {
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
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
    @Sql(scripts = "file:src/test/resources/sql/init-user-data.sql")
    @CsvFileSource(resources = "/data/users/successfullyUpdateUser.csv", delimiter = '|')
    void successfullyUpdateUser(long userId, String updateJson, String expectedResponseJson) throws Exception {
        mockMvc.perform(patch(basePath + "/" + userId)
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponseJson));
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/init-user-data.sql")
    @CsvFileSource(resources = "/data/users/failUpdateUser.csv", delimiter = '|')
    void failUpdateUser(long userId, int status, String updateJson, String expectedResponseJson) throws Exception {
        mockMvc.perform(patch(basePath + "/" + userId)
                        .content(updateJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponseJson));
    }

    @Test
    void getUsers() throws Exception {
        mockMvc.perform(get(basePath))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = "file:src/test/resources/sql/clean-db.sql")
    @Sql(scripts = "file:src/test/resources/sql/init-user-data.sql")
    void successfullyDeleteUser() throws Exception {
        String path = basePath + "/101";
        mockMvc.perform(get(path))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"id\":101,\"name\":\"user1\",\"email\":\"user1@user.com\"}"));
        mockMvc.perform(delete(path)
                )
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(get(path)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"message\":\"Пользователь с id(101) не найден!\"}"));
    }

    @Test
    void failDeleteUser() throws Exception {
        mockMvc.perform(delete(basePath + "/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}