package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

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
    ItemService itemService;
    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/successfullySaveItem.csv", delimiter = '|')
    public void successfullySaveItem(Long userId, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Accept", "application/json;charset=UTF-8");
        when(itemService.saveItem(any(), anyLong()))
                .thenReturn(objectMapper.readValue(expectedResponse, ItemDto.class));
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
        ItemDto creationDto = objectMapper.readValue(fileJson, ItemDto.class);
        if (userId != null && userId == 999) {
            when(itemService.saveItem(any(), anyLong()))
                    .thenThrow(new NotFoundException("Пользователь с id(999) не найден!"));
        } else if (creationDto.getRequestId() != null && creationDto.getRequestId() == 999) {
            when(itemService.saveItem(any(), anyLong()))
                    .thenThrow(new NotFoundException("Запрос с таким id(999) не существует!"));
        }
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
        when(itemService.updateItem(any(), anyLong(), anyLong()))
                .thenReturn(objectMapper.readValue(expectedResponse, ItemDto.class));
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
        if ("/999".equals(itemId)) {
            when(itemService.getItem(anyLong(), anyLong()))
                    .thenThrow(new NotFoundException("Вещь с id(999) не найдена!"));
        } else {
            when(itemService.getItem(anyLong(), anyLong()))
                    .thenReturn(objectMapper.readValue(expectedResponse, Item.class));
        }
        mockMvc.perform(get(basePath + itemId)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/getItems.csv", delimiter = '|')
    void getItems(long userId, int status, String expectedResponse) throws Exception {
        when(itemService.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(objectMapper.readValue(expectedResponse, new TypeReference<>() {
                }));
        mockMvc.perform(get(basePath)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/searchItems.csv", delimiter = '|')
    void searchItems(String fileJson, String expectedResponse) throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(objectMapper.readValue(expectedResponse, new TypeReference<>() {
                }));
        mockMvc.perform(get(basePath + "/search?text=" + (fileJson == null ? "" : fileJson)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/successfullySaveComment.csv", delimiter = '|')
    void successfullySaveComment(Long userId, String itemIdPath, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        CommentDto dto = objectMapper.readValue(String.format(expectedResponse + "\"%s\"}", LocalDateTime.now()), CommentDto.class);
        when(itemService.saveComment(anyLong(), anyLong(), any()))
                .thenReturn(dto);
        mockMvc.perform(post(basePath + itemIdPath)
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

}