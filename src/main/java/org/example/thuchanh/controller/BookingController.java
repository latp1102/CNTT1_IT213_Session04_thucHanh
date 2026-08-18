package org.example.thuchanh.controller;

import jakarta.validation.Valid;
import org.example.thuchanh.model.dto.request.ChatRequest;
import org.example.thuchanh.service.BookingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public String createBooking(@Valid @RequestBody ChatRequest chatRequest) {
        return bookingService.createBooking(chatRequest);
    }
}
