package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    final EasyRandom generator = new EasyRandom();
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @CsvFileSource(resources = "/data/requests/successfullySaveItemRequest.csv", delimiter = '|')
    void successfullySaveItemRequest(Long userId, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        ItemRequestDto dto = objectMapper.readValue(String.format(expectedResponse + "\"%s\"}",
                LocalDateTime.now()), ItemRequestDto.class);
        when(itemRequestService.save(any(), anyLong()))
                .thenReturn(dto);
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
        ItemRequestDto dto = generator.nextObject(ItemRequestDto.class);
        dto.setItems(null);
        ItemRequestDto dto1 = generator.nextObject(ItemRequestDto.class);
        dto1.setItems(null);
        when(itemRequestService.getAllByUserId(101))
                .thenReturn(List.of(dto, dto1));
        String response = mockMvc.perform(get(basePath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(2, response.split("},\\{").length);
    }

    @Test
    void failGetItemRequestsByUser() throws Exception {
        String response = "{\"message\":\"Пользователь с id(999) не найден!\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "999");
        headers.add("Content-Type", "application/json;charset=UTF-8");
        when(itemRequestService.getAllByUserId(999))
                .thenThrow(new NotFoundException("Пользователь с id(999) не найден!"));
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
        if ("/999".equals(itemRequestIdPath) || userId == 999) {
            when(itemRequestService.getById(Long.parseLong(itemRequestIdPath.substring(1)), userId))
                    .thenThrow(new NotFoundException(objectMapper.readValue(expectedResponse, ObjectNode.class).get("message").asText()));
        } else {
            when(itemRequestService.getById(Long.parseLong(itemRequestIdPath.substring(1)), userId))
                    .thenReturn(objectMapper.readValue(expectedResponse + "\"" + LocalDateTime.now() + "\"}",
                            ItemRequest.class));
        }
        mockMvc.perform(get(basePath + itemRequestIdPath)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @Test
    void getItemRequestsStartingFrom() throws Exception {
        String requestParams = "/all?size=%d&from=%d";
        int size = 3;
        int from = 0;
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", "101");
        headers.add("Content-Type", "application/json;charset=UTF-8");
        List<ItemRequestDto> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ItemRequestDto dto = generator.nextObject(ItemRequestDto.class);
            dto.setItems(null);
            list.add(dto);
        }
        when(itemRequestService.getAllStartingFrom(from, size, 101))
                .thenReturn(list);
        String response = mockMvc.perform(get(basePath + String.format(requestParams, size, from))
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(size - from, response.split("},\\{").length);
        from = 1;
        list.remove(0);
        when(itemRequestService.getAllStartingFrom(from, size, 101))
                .thenReturn(list);
        response = mockMvc.perform(get(basePath + String.format(requestParams, size, from))
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(size - from, response.split("},\\{").length);
    }

}