package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    final String basePath = "/items";
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemAndCommentClient itemAndCommentClient;
    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/successfullySaveItem.csv", delimiter = '|')
    public void successfullySaveItem(Long userId, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        when(itemAndCommentClient.saveItem(any(), anyLong()))
                .thenReturn(ResponseEntity.status(201).body(expectedResponse));
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/failSaveItem.csv", delimiter = '|')
    public void failSaveItem(Long userId, int status, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.add("X-Sharer-User-Id", userId.toString());
        }
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(post(basePath)
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(expectedResponse == null ? "" : expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/successfullyUpdateItem.csv", delimiter = '|')
    public void successfullyUpdateItem(Long userId, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        when(itemAndCommentClient.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(expectedResponse));
        mockMvc.perform(patch(basePath + "/101")
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/getItem.csv", delimiter = '|')
    void getItem(String itemId, long userId, int status, String expectedResponse) throws Exception {
        when(itemAndCommentClient.getItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.status(status).body(expectedResponse));
        mockMvc.perform(get(basePath + itemId)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/getItems.csv", delimiter = '|')
    void getItems(long userId, int status, String expectedResponse) throws Exception {
        when(itemAndCommentClient.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.status(status).body(expectedResponse));
        mockMvc.perform(get(basePath)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/successfullySaveComment.csv", delimiter = '|')
    void successfullySaveComment(Long userId, String itemIdPath, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        expectedResponse = String.format(expectedResponse + "\"%s\"}", LocalDateTime.now());
        when(itemAndCommentClient.saveComment(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok(expectedResponse));
        mockMvc.perform(post(basePath + itemIdPath)
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/failSaveComment.csv", delimiter = '|')
    void failSaveComment(Long userId, String itemIdPath, String fileJson) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(post(basePath + itemIdPath)
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}