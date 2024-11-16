package com.example.hotelswebapp;

import com.example.hotelswebapp.controllers.PostController;
import com.example.hotelswebapp.entity.HotelRoomEntity;
import com.example.hotelswebapp.entity.Service;
import com.example.hotelswebapp.services.HotelRoomService;
import com.example.hotelswebapp.services.ServiceOfServices;
import com.example.hotelswebapp.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HotelsWebAppApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelRoomService hotelRoomService;

    @MockBean
    private ServiceOfServices serviceOfServices;

    @MockBean
    private UserService userService;

    @InjectMocks
    private PostController postController;

    private HotelRoomEntity hotelRoomEntity;
    private List<MultipartFile> files;
    private Set<Service> services;

    @BeforeEach
    public void setUp() {
        hotelRoomEntity = new HotelRoomEntity();
        hotelRoomEntity.setName("Test Room");
        hotelRoomEntity.setPricePerDay(100);
        hotelRoomEntity.setAmountOfSleepers(2);
        hotelRoomEntity.setDescription("Test Description");

        Service service1 = new Service();
        service1.setId(1);
        service1.setName("WiFi");

        Service service2 = new Service();
        service2.setId(2);
        service2.setName("Breakfast");

        services = new HashSet<>();
        services.add(service1);
        services.add(service2);

        hotelRoomEntity.setServices(services);

        files = new ArrayList<>();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testPostingAdvertisement() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "images",
                "test-image.jpg",
                "image/jpeg",
                "Test Image Content".getBytes()
        );

        mockMvc.perform(multipart("/posting")
                        .file(mockFile)
                        .param("otherParam", "value")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
