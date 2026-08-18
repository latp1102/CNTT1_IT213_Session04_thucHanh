package org.example.thuchanh.service;

import jakarta.validation.Valid;
import org.example.thuchanh.model.dto.request.BookingRequestDTO;
import org.example.thuchanh.model.dto.request.ChatRequest;
import org.example.thuchanh.model.entity.Booking;
import org.example.thuchanh.repository.BookingRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;

import java.time.LocalDateTime;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final ChatClient chatClient;
    private final BeanOutputConverter<BookingRequestDTO> beanOutputConverter = new BeanOutputConverter<>(BookingRequestDTO.class);

    public BookingService(BookingRepository bookingRepository,ChatClient.Builder chatClient) {
        this.bookingRepository = bookingRepository;
        this.chatClient = chatClient.build();
    }

    public String createBooking(ChatRequest chatRequest) {
        String output = chatClient.prompt()
                .system("""
                        Bạn là nhân viên lễ tân, bạn hãy trích suất thông tin đặt phòng từ tin nhắn
                        người đùng gửi lên và chuyển đổi sang đối tượng BookingRequestDTO :
                        rawTex : %s
                        format : {%s}
                        """.formatted(chatRequest.getMessage(), beanOutputConverter.getFormat()))
                .user(chatRequest.getMessage())
                .call()
                .content();
        BookingRequestDTO bookingRequestDTO = beanOutputConverter.convert(output);
        return convertAndSaveBooking(bookingRequestDTO);
    }

    private String convertAndSaveBooking(@Valid BookingRequestDTO bookingRequestDTO) {
        try {
            Booking booking = Booking
                    .builder()
                    .customerName(bookingRequestDTO.getCustomerName())
                    .budget(bookingRequestDTO.getBudget())
                    .checkInDate(bookingRequestDTO.getCheckInDate())
                    .checkOutDate(bookingRequestDTO.getCheckOutDate())
                    .numberOfPeople(bookingRequestDTO.getNumberOfPeople())
                    .createdDate(LocalDateTime.now())
                    .build();

            bookingRepository.save(booking);
            return "Đặt phòng thành công";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
