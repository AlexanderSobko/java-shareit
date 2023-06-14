package ru.practicum.shareit.item;

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

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integration-test.properties")
class ItemIntegrationTest {

    private final String basePath = "/items";
    private final MockMvc mockMvc;

    @Autowired
    ItemIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/items/successfullySaveItem.csv", delimiter = '|')
    public void successfullySaveItem(Long userId, String fileJson, String expectedResponse) throws Exception {
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
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/items/successfullyUpdateItem.csv", delimiter = '|')
    public void successfullyUpdateItem(Long userId, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(patch(basePath + "/101")
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/items/failUpdateItem.csv", delimiter = '|')
    public void failUpdateItem(Long userId, int status, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        if (userId != null) {
            headers.add("X-Sharer-User-Id", userId.toString());
        }
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(patch(basePath + "/101")
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(
                        containsString(expectedResponse == null ? "" : expectedResponse)));
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/items/getItem.csv", delimiter = '|')
    void getItem(String itemId, long userId, int status, String expectedResponse) throws Exception {
        mockMvc.perform(get(basePath + itemId)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/items/getItems.csv", delimiter = '|')
    void getItems(long userId, int status, String expectedResponse) throws Exception {
        mockMvc.perform(get(basePath)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/items/searchItems.csv", delimiter = '|')
    void searchItems(String fileJson, String expectedResponse) throws Exception {
        mockMvc.perform(get(basePath + "/search?text=" + (fileJson == null ? "" : fileJson)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/items/successfullySaveComment.csv", delimiter = '|')
    void successfullySaveComment(Long userId, String itemIdPath, String fileJson, String expectedResponse) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", userId.toString());
        headers.add("Content-Type", "application/json;charset=UTF-8");
        mockMvc.perform(post(basePath + itemIdPath)
                        .content(fileJson)
                        .headers(headers))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
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