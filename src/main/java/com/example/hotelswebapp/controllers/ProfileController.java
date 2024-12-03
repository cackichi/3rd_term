package com.example.hotelswebapp.controllers;

import com.example.hotelswebapp.entity.HotelRoomEntity;
import com.example.hotelswebapp.entity.ReviewOfRoom;
import com.example.hotelswebapp.entity.UserEntity;
import com.example.hotelswebapp.services.*;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@AllArgsConstructor
public class ProfileController {
    private final HotelRoomService hotelRoomService;
    private final ReservationService reservationService;
    private final UserService userService;
    private final ReviewOfRoomService reviewOfRoomService;
    private final ServiceOfServices serviceOfServices;

    @GetMapping("/profile")
    public String showProfile(Model model) {
        userService.addUserInfo(model);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userService.getAuthUser(auth.getName());
        List<ReviewOfRoom> reviewOfRooms = user.getReviewOfRooms();
        int fullPrice = hotelRoomService.getFullPriceForUser(user.getId());
        model.addAttribute("reviews", reviewOfRooms);
        model.addAttribute("userEntity", user);
        model.addAttribute("fullPrice", fullPrice);
        return "profile";
    }

    @PostMapping("/delete-room")
    public String deleteRoomByUser(@RequestParam("roomIdForDelete") int id) {
        hotelRoomService.deleteRoomById(id);
        return "redirect:/profile";
    }

    @PostMapping("/delete-photo")
    public String deletePhoto(@RequestParam("roomId") int id,
                               @RequestParam("photoToDelete") String photo,
                               RedirectAttributes redirectAttributes){
        redirectAttributes.addAttribute("roomIdForEdit",id);
        if(hotelRoomService.findRoomById(id).getPhotos().size() > 1) {
            hotelRoomService.deletePhotoFromRoom(photo, id);
        } else {
            redirectAttributes.addFlashAttribute("photoError","Прежде чем удалить последнее фото,\nдобавьте еще одно");
        }
        return "redirect:/edit-room";
    }

    @PostMapping("/delete-reservation")
    public String deleteReservationUser(@RequestParam("reservationIdForDelete") int id) {
        reservationService.delete(id);
        return "redirect:/profile";
    }

    @PostMapping("/delete-review")
    public String deleteReview(@RequestParam("reviewIdToDelete") int id){
        reviewOfRoomService.deleteReview(id);
        return "redirect:/profile";
    }

    @PostMapping("/edit-review")
    public String editReview(@RequestParam("reviewIdForEdit") int id,
                             @RequestParam("changedRatingOfReview") double rating,
                             @RequestParam("changedTextOfReview") String text){
        ReviewOfRoom reviewOfRoom = reviewOfRoomService.findById(id);
        reviewOfRoom.setText(text);
        reviewOfRoom.setRating(rating);
        reviewOfRoomService.saveReview(reviewOfRoom);
        return "redirect:/profile";
    }

    @GetMapping("/edit-room")
    public String editRoomGet(Model model, @RequestParam("roomIdForEdit") int id){
                userService.addUserInfo(model);
        HotelRoomEntity hotelRoomEntity = hotelRoomService.findRoomById(id);

        model.addAttribute("allServices", serviceOfServices.findAll());
        model.addAttribute("photos", hotelRoomEntity.getPhotos());
        model.addAttribute("services", hotelRoomEntity.getServices());
        model.addAttribute("room", hotelRoomEntity);
        return "edit";

    }

    @PostMapping("/edit-room-post")
    public String editRoomPost(@ModelAttribute("room") HotelRoomEntity room,
                               @RequestParam(name = "images", required = false) MultipartFile[] files,
                               @RequestParam("roomIdForEdit") int id,
                               Model model, RedirectAttributes redirectAttributes){
        userService.addUserInfo(model);
        HotelRoomEntity editRoom = hotelRoomService.findRoomById(id);
        List<String> photos = editRoom.getPhotos();
        room.setPhotos(hotelRoomService.uploadPhoto(files,photos));
        room.setId(editRoom.getId());
        room.setReservations(editRoom.getReservations());
        hotelRoomService.saveHotelRoom(room);

        model.addAttribute("photos", photos);
        model.addAttribute("total_services",room.getServices());
        model.addAttribute("room",room);
        redirectAttributes.addAttribute("roomIdForEdit",id);
        return "redirect:/edit-room";
    }
}
