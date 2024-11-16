package com.example.hotelswebapp.controllers;

import com.example.hotelswebapp.entity.HotelRoomEntity;
import com.example.hotelswebapp.services.HotelRoomService;
import com.example.hotelswebapp.services.ReviewOfRoomService;
import com.example.hotelswebapp.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;


@Controller
@AllArgsConstructor
public class MainController {
    private final UserService userService;
    private final HotelRoomService hotelRoomService;
    private final ReviewOfRoomService reviewOfRoomService;

    @GetMapping("/")
    public String mainPage(Model model, @PageableDefault(size = 3) Pageable pageable,
                           @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        pageable = PageRequest.of(page, pageable.getPageSize());
        Page<HotelRoomEntity> rooms = hotelRoomService.pageOfRooms(pageable);

        model.addAttribute("rooms", rooms);
        model.addAttribute("averageRatings", reviewOfRoomService.getAverageOfAllRooms(rooms));
        userService.addUserInfo(model);

        return "main";
    }

    @GetMapping("/search")
    public String search(Model model, @PageableDefault Pageable pageable,
                         @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                         @RequestParam(name = "roomName", required = false, defaultValue = " ") String roomName,
                         @RequestParam(name = "checkInDate", required = false) LocalDate checkInDate,
                         @RequestParam(name = "checkOutDate", required = false) LocalDate checkOutDate,
                         @RequestParam(name = "amountOfSleepers") int amountOfSleepers,
                         @RequestParam(name = "minPrice", required = false, defaultValue = "0") int minPrice,
                         @RequestParam(name = "maxPrice", required = false, defaultValue = "0") int maxPrice,
                         @RequestParam(name = "sortBy", defaultValue = "default") String sortBy,
                         RedirectAttributes redirectAttributes) {
        userService.addUserInfo(model);
        if (checkInDate != null && checkOutDate != null) {
            if (checkOutDate.isBefore(checkInDate) || checkOutDate.equals(checkInDate)) {
                redirectAttributes.addFlashAttribute("errorOfIncorrectDate", "Выберите дату правильно");
                return "redirect:/";
            }
        }
        if (minPrice != 0 && maxPrice != 0) {
            if (minPrice == maxPrice || minPrice > maxPrice) {
                redirectAttributes.addFlashAttribute("errorOfIncorrectInputPrice", "Выберите диапазон цен правильно");
                return "redirect:/";
            }
        }
        Sort sort = switch (sortBy) {
            case "byPrice" -> Sort.by("pricePerDay");
            case "byPriceDescending" -> Sort.by("pricePerDay").descending();
            case "bySleepers" -> Sort.by("amountOfSleepers");
            case "bySleepersDescending" -> Sort.by("amountOfSleepers").descending();
            case "byAlphabet" -> Sort.by("name");
            case "byAlphabetDescending" -> Sort.by("name").descending();
            default -> Sort.unsorted();
        };
        pageable = PageRequest.of(page, pageable.getPageSize(), sort);

        Page<HotelRoomEntity> result = hotelRoomService.searchRooms(
                roomName, minPrice, maxPrice, amountOfSleepers, checkInDate, checkOutDate, pageable);

        model.addAttribute("averageRatings", reviewOfRoomService.getAverageOfAllRooms(result));
        model.addAttribute("isEmpty", result.getContent().isEmpty());
        model.addAttribute("result", result);
        return "search_results";
    }

    @GetMapping("/about-us")
    public String aboutUs(Model model) {
        userService.addUserInfo(model);
        return "about";
    }
}
