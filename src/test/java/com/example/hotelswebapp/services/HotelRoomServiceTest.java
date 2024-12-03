package com.example.hotelswebapp.services;

import com.example.hotelswebapp.entity.HotelRoomEntity;
import com.example.hotelswebapp.entity.Reservation;
import com.example.hotelswebapp.repos.HotelRoomRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class HotelRoomServiceTest {

    @Mock
    private HotelRoomRepo hotelRoomRepo;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private HotelRoomService hotelRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindRoomById() {
        HotelRoomEntity room = new HotelRoomEntity();
        room.setId(1);

        when(hotelRoomRepo.findById(1)).thenReturn(room);

        HotelRoomEntity foundRoom = hotelRoomService.findRoomById(1);

        assertNotNull(foundRoom);
        assertEquals(1, foundRoom.getId());
    }

    @Test
    void testGetFullPriceForUser() {
        Reservation reservation1 = mock(Reservation.class);
        Reservation reservation2 = mock(Reservation.class);
        HotelRoomEntity room1 = mock(HotelRoomEntity.class);
        HotelRoomEntity room2 = mock(HotelRoomEntity.class);

        when(reservationService.findByUserId(anyInt())).thenReturn(Arrays.asList(reservation1, reservation2));

        when(reservation1.getHotelRoomEntity()).thenReturn(room1);
        when(room1.getPricePerDay()).thenReturn(100);

        when(reservation2.getHotelRoomEntity()).thenReturn(room2);
        when(room2.getPricePerDay()).thenReturn(150);

        when(reservation1.getDateOfReservation()).thenReturn(LocalDate.now());
        when(reservation1.getDateOfEviction()).thenReturn(LocalDate.now().plusDays(5));
        when(reservation2.getDateOfReservation()).thenReturn(LocalDate.now());
        when(reservation2.getDateOfEviction()).thenReturn(LocalDate.now().plusDays(3));

        int price = hotelRoomService.getFullPriceForUser(1);

        assertEquals(500 + 450, price);

        System.out.println("Результат расчета цены для пользователя: " + price);
    }


    @Test
    void testSaveHotelRoom() {
        HotelRoomEntity room = new HotelRoomEntity();
        room.setId(1);

        hotelRoomService.saveHotelRoom(room);

        verify(hotelRoomRepo).save(room);
    }

    @Test
    void testFindAllRooms() {
        List<HotelRoomEntity> rooms = Arrays.asList(new HotelRoomEntity(), new HotelRoomEntity());

        when(hotelRoomRepo.findAll()).thenReturn(rooms);

        List<HotelRoomEntity> result = hotelRoomService.findAllRooms();

        assertEquals(2, result.size());
    }
}
