package com.example.hotelswebapp.services;

import com.example.hotelswebapp.entity.HotelRoomEntity;
import com.example.hotelswebapp.entity.Reservation;
import com.example.hotelswebapp.repos.HotelRoomRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Getter
public class HotelRoomService {
    private final HotelRoomRepo hotelRoomRepo;
    private final ReservationService reservationService;
    private final String PHOTOS_PATH = "C:\\Users\\Фёдор\\Desktop\\3 курс\\курсач\\HotelsWebApp\\src\\main\\resources\\static\\photos";

    public HotelRoomEntity findRoomById(int id) {
        return hotelRoomRepo.findById(id);
    }

    private String generateUniqueFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }

    public List<String> uploadPhoto(MultipartFile[] files, List<String> photos) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = generateUniqueFileName(file.getOriginalFilename());
                    File uploadFile = new File(PHOTOS_PATH + "\\" + fileName);
                    file.transferTo(uploadFile);
                    photos.add(fileName);
                } catch (IOException e) {
                    System.out.println("Файл не найден");
                }
            }
        }
        return photos;
    }

    public int getFullPriceForUser(int userEntityId) {
        int fullPrice = 0;
        List<Reservation> reservations = reservationService.findByUserId(userEntityId);
        for (Reservation reservation : reservations) {
            HotelRoomEntity hotelRoomEntity = reservation.getHotelRoomEntity();
            LocalDate checkIn = reservation.getDateOfReservation();
            LocalDate checkOut = reservation.getDateOfEviction();
            long numOfDays = ChronoUnit.DAYS.between(checkIn, checkOut);
            fullPrice += hotelRoomEntity.getPricePerDay() * numOfDays;
        }
        return fullPrice;
    }

    @Transactional
    public void saveHotelRoom(HotelRoomEntity hotelRoomEntity) {
        hotelRoomRepo.save(hotelRoomEntity);
    }

    public List<HotelRoomEntity> findAllRooms() {
        return hotelRoomRepo.findAll();
    }

    @Transactional
    public void deleteRoomById(int id) {
        List<String> photosToDelete = hotelRoomRepo.findPhotosById(id);
        hotelRoomRepo.deleteById(id);
        for (String photo : photosToDelete) {
            Path path = Paths.get(PHOTOS_PATH + "\\", photo);
            try {
                Files.delete(path);
            } catch (IOException e) {
                System.out.println("Файл не найден");
            }
        }
    }

    @Transactional
    public void deletePhotoFromRoom(String photo, int roomId) {
        HotelRoomEntity room = hotelRoomRepo.findById(roomId);
        if (room != null) {
            List<String> photos = room.getPhotos();
            if (photos != null) {
                if (photos.remove(photo)) {
                    room.setPhotos(photos);
                    hotelRoomRepo.save(room);
                    Path path = Paths.get(PHOTOS_PATH, photo);
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        System.out.println("Файл не найден");
                    }
                }
            }
        }
    }

    public Page<HotelRoomEntity> pageOfRooms(Pageable pageable) {
        return hotelRoomRepo.findAll(pageable);
    }

}
