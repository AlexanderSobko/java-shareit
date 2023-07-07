package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    final String basePath = "/requests";
    @MockBean
    ItemRequestClient itemRequestClient;
    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/successfullySaveItemRequest.csv", delimiter = '|')
    void successfullySaveItemRequest(Long userId, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        ResponseEntity<Object> response = ResponseEntity.status(201).body(String.format(expectedResponse + "\"%s\"}",
                LocalDateTime.now()));
        when(itemRequestClient.save(any(), anyLong()))
                .thenReturn(response);
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @Test
    void getItemRequestsByUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "101");
        headers.add("Content-Type", "application/json;charset=UTF-8");
        when(itemRequestClient.getAllByUserId(101L))
                .thenReturn(ResponseEntity.ok(List.of()));
        String response = mockMvc.perform(get(basePath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(0, response.length() > 2 ? response.split("},\\{").length : 0);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/getItemRequest.csv", delimiter = '|')
    void getItemRequest(String itemRequestIdPath, int status, Long userId, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        when(itemRequestClient.getById(Long.parseLong(itemRequestIdPath.substring(1)), userId))
                .thenReturn(ResponseEntity.ok(expectedResponse + "\"" + LocalDateTime.now() + "\"}"));
        mockMvc.perform(get(basePath + itemRequestIdPath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
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