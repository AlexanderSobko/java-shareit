package ru.practicum.shareit.request;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/sql/init-data.sql")
@TestPropertySource(locations = "classpath:application-integration-test.properties")
class ItemRequestIntegrationTest {

    private final String basePath = "/requests";
    private final MockMvc mockMvc;

    @Autowired
    ItemRequestIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/successfullySaveItemRequest.csv", delimiter = '|')
    void successfullySaveItemRequest(Long userId, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/getItemRequestsByUser.csv", delimiter = '|')
    void getItemRequestsByUser(Long userId, int expectedResponseSize) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        String response = mockMvc.perform(get(basePath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(expectedResponseSize, response.split("},\\{").length);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/getItemRequestsByUser.csv", delimiter = '|')
    void failGetItemRequestsByUser() throws Exception {
        String response = "{\"message\":\"Пользователь с id(999) не найден!\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "999");
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(get(basePath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(response));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/getItemRequest.csv", delimiter = '|')
    void getItemRequest(String itemRequestIdPath, int status, Long userId, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(get(basePath + itemRequestIdPath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/getItemRequestsStartingFrom.csv", delimiter = '|')
    void getItemRequestsStartingFrom(String sizeAndFromPath, Long userId, int expectedResponseSize) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        String response = mockMvc.perform(get(basePath + sizeAndFromPath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(expectedResponseSize, response.split("},\\{").length);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/failGetItemRequestsStartingFrom.csv", delimiter = '|')
    void failGetItemRequestsStartingFrom(String sizeAndFromPath, Long userId) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(get(basePath + sizeAndFromPath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}