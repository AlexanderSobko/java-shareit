package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSimpleDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    final String basePath = "/bookings";
    final EasyRandom generator = new EasyRandom();
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mockMvc;

    @Test
    void successfullySaveBooking() throws Exception {
        LocalDateTime startTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(1);
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(10);
        BookingSimpleDto dto = BookingSimpleDto.builder()
                .start(startTime)
                .end(endTime)
                .itemId(101)
                .userId(101)
                .build();
        String expectedResponse = String.format("{\"id\":1,\"start\":\"%s\",\"end\":\"%s\",\"booker\":{\"id\":101,\"name\":\"user1\",\"email\":\"user1@user.com\"},\"item\":{\"id\":103,\"name\":\"name3\",\"description\":\"description3\",\"owner\":{\"id\":102,\"name\":\"user2\",\"email\":\"user2@user.com\"},\"itemRequest\":null,\"rentCount\":0,\"available\":true,\"nextBooking\":null,\"lastBooking\":null,\"comments\":[]},\"status\":\"WAITING\"}", dto.getStart(), dto.getEnd());
        BookingDto responseDto = objectMapper.readValue(expectedResponse, BookingDto.class);
        when(bookingService.saveBooking(any(BookingSimpleDto.class), anyLong()))
                .thenReturn(responseDto);
        mockMvc.perform(post(basePath)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(dto.getUserId())))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/bookings/successfullyUpdateBookingStatus.csv", delimiter = '|')
    void updateBookingStatusByOwner(String path, long ownerId, String expectedResponse) throws Exception {
        LocalDateTime startTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(1);
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(10);
        expectedResponse = String.format("{\"id\":102,\"start\":\"%s\",\"end\":\"%s\",", startTime, endTime)
                + expectedResponse;
        when(bookingService.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(objectMapper.readValue(expectedResponse, BookingDto.class));
        mockMvc.perform(patch(basePath + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(ownerId)))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/data/bookings/successfullyGetBooking.csv", delimiter = '|')
    void successfullyGetBooking(String bookingId, long userId, String expectedResponse) throws Exception {
        LocalDateTime startTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(1);
        LocalDateTime endTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(10);
        expectedResponse = String.format("{\"id\":101,\"start\":\"%s\",\"end\":\"%s\"", startTime, endTime) + expectedResponse;
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(objectMapper.readValue(expectedResponse, Booking.class));
        mockMvc.perform(get(basePath + bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(userId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse));
    }

    @Test
    void getAllBookings() throws Exception {
        List<BookingDto> list = List.of(generator.nextObject(BookingDto.class));
        when(bookingService.getBookings(anyLong(), any(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(list);
        String response = mockMvc.perform(get(basePath + "?state=ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(104)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(response);
        assertEquals(list.size(), response.split("},\\{").length);
    }

    @Test
    void failGetBookingsUnknownState() throws Exception {
        when(bookingService.getBookings(anyLong(), any(), anyBoolean(), anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException());
        String response = mockMvc.perform(get(basePath + "?state=fjvkjvk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(104)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals("{\"error\":\"Unknown state: UNSUPPORTED_STATUS\"}", response);
    }

    @Test
    void getAllBookingsOwner() throws Exception {
        List<BookingDto> list = List.of(generator.nextObject(BookingDto.class));
        when(bookingService.getBookings(anyLong(), any(), anyBoolean(), anyInt(), anyInt()))
                .thenReturn(list);
        String response = mockMvc.perform(get(basePath + "/owner?state=FUTURE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", List.of(104)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(response);
        assertEquals(list.size(), response.split("},\\{").length);
    }
}