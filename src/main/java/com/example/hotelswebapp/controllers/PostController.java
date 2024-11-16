package com.example.hotelswebapp.controllers;

import com.example.hotelswebapp.entity.HotelRoomEntity;
import com.example.hotelswebapp.entity.Reservation;
import com.example.hotelswebapp.entity.ReviewOfRoom;
import com.example.hotelswebapp.entity.UserEntity;
import com.example.hotelswebapp.services.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Controller
public class PostController {
    private final UserService userService;
    private final ReservationService reservationService;
    private final HotelRoomService hotelRoomService;
    private final ReviewOfRoomService reviewOfRoomService;
    private final ServiceOfServices serviceOfServices;

    @GetMapping("/posting")
    public String postingAdvertisement(Model model) {
        userService.addUserInfo(model);
        model.addAttribute("hotelRoomEntity", new HotelRoomEntity());
        model.addAttribute("services", serviceOfServices.findAll());
        return "posting";
    }

    @PostMapping("/posting")
    public String postingAdvertisement(@ModelAttribute("hotelRoomEntity") HotelRoomEntity hotelRoomEntity
                                        ,@RequestParam("images") MultipartFile[] files, Model model) {
        userService.addUserInfo(model);
        List<String> photos = new ArrayList<>();
        hotelRoomEntity.setPhotos(hotelRoomService.uploadPhoto(files,photos));
        hotelRoomService.saveHotelRoom(hotelRoomEntity);

        return "redirect:/posting";
    }

    @GetMapping("/post")
    public String postById(Model model, @PageableDefault(size = 3) Pageable pageable, @RequestParam(name = "roomId") int id) {
        HotelRoomEntity hotelRoomEntity = hotelRoomService.findRoomById(id);
        List<Reservation> reservations = reservationService.findByRoomId(id);
        Page<ReviewOfRoom> reviewOfRooms = reviewOfRoomService.findByRoomId(id,pageable);

        model.addAttribute("room", hotelRoomEntity);
        model.addAttribute("reservations", reservations);
        model.addAttribute("reviews", reviewOfRooms);
        userService.addUserInfo(model);

        if(!reviewOfRooms.isEmpty()) {
            double rating = reviewOfRoomService.getAverageRating(reviewOfRoomService.findByRoomId(id));
            String formattedRating = String.format("%.1f", rating);
            model.addAttribute("rating", formattedRating);
        } else model.addAttribute("rating", "5,0");

        return "post";
    }
    @PostMapping("/reserve")
    public String reservation(RedirectAttributes redirectAttributes,
                              @RequestParam(name = "roomIdForReserve") int id,
                              @RequestParam(name = "checkInDate", required = false) LocalDate checkInDate,
                              @RequestParam(name = "checkOutDate", required = false) LocalDate checkOutDate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.getName().equals("anonymousUser")) {
            HotelRoomEntity room = hotelRoomService.findRoomById(id);

            if (checkInDate != null && checkOutDate != null) {
                if (checkOutDate.isBefore(checkInDate) || checkOutDate.equals(checkInDate)) {
                    redirectAttributes.addFlashAttribute("errorOfIncorrectDate", "Выберите дату правильно");
                    return "post";
                }
                for (Reservation reservation : room.getReservations()) {
                    if (!((checkInDate.isBefore(reservation.getDateOfReservation()) && checkOutDate.isBefore(reservation.getDateOfReservation())) ||
                            (checkInDate.isAfter(reservation.getDateOfEviction()) && checkOutDate.isAfter(reservation.getDateOfEviction())))) {
                        redirectAttributes.addFlashAttribute("errorOfExistReservation", "На эти даты уже забронировано");
                        return "post";
                    }
                }

                UserEntity userEntity = userService.getAuthUser(auth.getName());
                Reservation newReservation = Reservation.builder()
                        .dateOfReservation(checkInDate)
                        .dateOfEviction(checkOutDate)
                        .hotelRoomEntity(room)
                        .userEntity(userEntity)
                        .build();
                reservationService.save(newReservation);
                redirectAttributes.addAttribute("roomId",id);
            }
            return "redirect:/post";
        } else return "login";
    }
    @PostMapping("/add-review")
    public String addReview(RedirectAttributes redirectAttributes,
                            @RequestParam(name = "reviewText") String reviewText,
                            @RequestParam(name = "rating") double rating,
                            @RequestParam(name = "roomIdToAddReview") int id,
                            @RequestParam(name = "userNameToAddReview") String username){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.getName().equals("anonymousUser")) {
            ReviewOfRoom reviewOfRoom = ReviewOfRoom.builder()
                    .datePost(LocalDate.now())
                    .text(reviewText)
                    .rating(rating)
                    .hotelRoomEntity(hotelRoomService.findRoomById(id))
                    .userEntity(userService.findByLogin(username).get())
                    .build();
            reviewOfRoomService.saveReview(reviewOfRoom);

            redirectAttributes.addAttribute("roomId", id);
            return "redirect:/post";
        } else return "login";
    }
}
