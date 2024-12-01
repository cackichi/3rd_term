package com.example.hotelswebapp.controllers;

import com.example.hotelswebapp.entity.HotelRoomEntity;
import com.example.hotelswebapp.services.HotelRoomService;
import com.example.hotelswebapp.services.ReviewOfRoomService;
import com.example.hotelswebapp.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MainControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private HotelRoomService hotelRoomService;
    @Mock
    private ReviewOfRoomService reviewOfRoomService;
    @InjectMocks
    private MainController mainController;
    private MockMvc mockMvc;
    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        List<HotelRoomEntity> rooms = Arrays.asList(
                new HotelRoomEntity(), new HotelRoomEntity()
        );
        Page<HotelRoomEntity> page = new PageImpl<>(rooms, PageRequest.of(0, 3), 1);

        when(hotelRoomService.pageOfRooms(any(Pageable.class))).thenReturn(page);

        when(reviewOfRoomService.getAverageOfAllRooms(any())).thenReturn(Arrays.asList("4.5", "4.6"));
    }

    @Test
    void mainPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attributeExists("averageRatings"));
        verify(hotelRoomService).pageOfRooms(any(Pageable.class));
        verify(reviewOfRoomService).getAverageOfAllRooms(any());
    }

    @Test
    void mainPagePagination() throws Exception {
        mockMvc.perform(get("/").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("rooms"));

        mockMvc.perform(get("/").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("rooms"));
    }

    @Test
    void searchWithParameters() throws Exception {
        mockMvc.perform(get("/search")
                        .param("roomName", "testRoom")
                        .param("checkInDate", "2024-01-01")
                        .param("checkOutDate", "2024-01-05")
                        .param("amountOfSleepers", "2")
                        .param("minPrice", "100")
                        .param("maxPrice", "200")
                        .param("sortBy", "byPrice"))
                .andExpect(status().isOk())
                .andExpect(view().name("search_results"));
    }

}
