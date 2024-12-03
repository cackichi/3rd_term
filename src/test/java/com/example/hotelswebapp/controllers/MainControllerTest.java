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

import java.util.ArrayList;
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

        List<HotelRoomEntity> rooms = new ArrayList<>();

        HotelRoomEntity room1 = new HotelRoomEntity();
        room1.setId(1);
        room1.setName("Deluxe Room");
        room1.setAmountOfSleepers(2);
        room1.setPricePerDay(150);
        room1.setPhotos(Arrays.asList("photo1.jpg", "photo2.jpg"));

        HotelRoomEntity room2 = new HotelRoomEntity();
        room2.setId(2);
        room2.setName("Standard Room");
        room2.setAmountOfSleepers(1);
        room2.setPricePerDay(120);
        room2.setPhotos(Arrays.asList("photo3.jpg", "photo4.jpg"));

        HotelRoomEntity room3 = new HotelRoomEntity();
        room3.setId(3);
        room3.setName("Suite Room");
        room3.setAmountOfSleepers(3);
        room3.setPricePerDay(200);
        room3.setPhotos(Arrays.asList("photo5.jpg", "photo6.jpg"));

        rooms.add(room1);
        rooms.add(room2);
        rooms.add(room3);

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

        System.out.println("Main page test completed successfully.");
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

        System.out.println("Main page pagination test completed successfully.");
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

        System.out.println("Search with parameters test completed successfully.");
    }

}
