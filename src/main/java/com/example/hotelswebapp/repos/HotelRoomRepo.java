package com.example.hotelswebapp.repos;

import com.example.hotelswebapp.entity.HotelRoomEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRoomRepo extends JpaRepository<HotelRoomEntity, Long> {
    @NonNull
    Page<HotelRoomEntity> findAll(@NonNull Pageable pageable);
    @NonNull
    List<HotelRoomEntity> findAll();
    HotelRoomEntity findById(int id);
    void deleteById(int id);
    @Query("SELECT photos FROM HotelRoomEntity WHERE id = :id")
    List<String> findPhotosById(@Param("id") int id);
    @Query("SELECT h FROM HotelRoomEntity h WHERE " +
            "(:roomName IS NULL OR LOWER(h.name) LIKE LOWER(CONCAT('%', :roomName, '%'))) AND " +
            "(:minPrice IS NULL OR h.pricePerDay >= :minPrice) AND " +
            "(:maxPrice IS NULL OR h.pricePerDay <= :maxPrice) AND " +
            "(:amountOfSleepers IS NULL OR h.amountOfSleepers >= :amountOfSleepers) AND " +
            "(:checkInDate IS NULL OR :checkOutDate IS NULL OR NOT EXISTS (" +
            "SELECT r FROM Reservation r WHERE r.hotelRoomEntity = h AND " +
            "(:checkInDate >= r.dateOfEviction OR :checkOutDate <= r.dateOfReservation)" +
            "))")
    Page<HotelRoomEntity> searchRooms(@Param("roomName") String roomName,
                                      @Param("minPrice") Integer minPrice,
                                      @Param("maxPrice") Integer maxPrice,
                                      @Param("amountOfSleepers") Integer amountOfSleepers,
                                      @Param("checkInDate") LocalDate checkInDate,
                                      @Param("checkOutDate") LocalDate checkOutDate,
                                      Pageable pageable);
}
