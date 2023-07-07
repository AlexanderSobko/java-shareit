package ru.practicum.shareit.booking;

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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "file:src/test/resources/sql/init-data.sql")
@TestPropertySource(locations = "classpath:application-integration-test.properties")
class BookingIntegrationTest {

    private final String basePath = "/bookings";
    private final MockMvc mockMvc;

    @Autowired
    BookingIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/clean-db.sql")
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/bookings/successfullySaveBooking.csv", delimiter = '|')
    void successfullySaveBooking(long userId, long itemId, int start, int end, String expectedResponse) throws Exception {
        LocalDateTime startTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(start);
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(end);
        expectedResponse = String.format(expectedResponse, startTime, endTime);
        String body = String.format("{\"itemId\": %d, \"start\": \"%s\", \"end\": \"%s\"}", itemId, startTime, endTime);
        mockMvc.perform(post(basePath)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/bookings/failSaveBooking.csv", delimiter = '|')
    void failSaveBooking(long userId, long itemId, int start, int end, int status, String expectedResponse) throws Exception {
        LocalDateTime startTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(start);
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(end);
        expectedResponse = expectedResponse == null ? "" : String.format(expectedResponse, startTime, endTime);
        String body = String.format("{\"itemId\": %d, \"start\": \"%s\", \"end\": \"%s\"}", itemId, startTime, endTime);
        mockMvc.perform(post(basePath)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/bookings/successfullyUpdateBookingStatus.csv", delimiter = '|')
    void successfullyUpdateBookingStatus(String path, long ownerId, String expectedResponse) throws Exception {
        mockMvc.perform(patch(basePath + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(ownerId)))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/bookings/failUpdateBookingStatus.csv", delimiter = '|')
    void failUpdateBookingStatus(String path, int status, long ownerId, String expectedResponse) throws Exception {
        expectedResponse = expectedResponse == null ? "" : expectedResponse;
        mockMvc.perform(patch(basePath + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(ownerId)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/bookings/successfullyGetBooking.csv", delimiter = '|')
    void successfullyGetBooking(String bookingId, long userId, String expectedResponse) throws Exception {
        expectedResponse = expectedResponse == null ? "" : expectedResponse;
        mockMvc.perform(get(basePath + bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/bookings/failGetBooking.csv", delimiter = '|')
    void failGetBooking(String bookingId, long userId, String expectedResponse) throws Exception {
        expectedResponse = expectedResponse == null ? "" : expectedResponse;
        mockMvc.perform(get(basePath + bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(containsString(expectedResponse)));
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/clean-db.sql")
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/bookings/getAllBookings.csv", delimiter = '|')
    void getAllBookings(String state, long userId, int expectedResponseSize) throws Exception {
        String response = mockMvc.perform(get(basePath + state)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(response);
        assertEquals(expectedResponseSize, response.split("},\\{").length);
    }

    @ParameterizedTest
    @Sql(scripts = "file:src/test/resources/sql/clean-db.sql")
    @Sql(scripts = "file:src/test/resources/sql/init-data.sql")
    @CsvFileSource(resources = "/data/bookings/getAllBookingsOwner.csv", delimiter = '|')
    void getAllBookingsOwner(String state, long userId, int expectedResponseSize) throws Exception {
        String response = mockMvc.perform(get(basePath + state)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(response);
        assertEquals(expectedResponseSize, response.split("},\\{").length);
    }

}