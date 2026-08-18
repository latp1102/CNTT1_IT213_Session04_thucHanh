package org.example.thuchanh.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.example.thuchanh.model.dto.request.BookingRequestDTO;
import org.example.thuchanh.model.dto.request.ChatRequest;
import org.example.thuchanh.model.entity.Booking;
import org.example.thuchanh.repository.BookingRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingService {
    public static List<String> memory = new ArrayList<>();

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
                        Bạn là nhân viên lễ tân. Hôm nay là ngày %s 
                        , bạn hãy trích suất thông tin đặt phòng từ tin nhắn
                        người đùng gửi lên, nhớ là không được bịa ra và chuyển đổi sang đối tượng BookingRequestDTO, lưu ý date dưới dạng yyyy-mm-ddThh:mm:ss :
                        lịch sử chat lúc đó :
                        rawTex : %s
                        format : {%s}
                        """.formatted(LocalDateTime.now().toString(), memory.stream().collect(Collectors.joining("\n")), chatRequest.getMessage(), beanOutputConverter.getFormat()))
                .user(chatRequest.getMessage())
                .options(ChatOptions.builder().temperature(0.3))
                .call()
                .content();
        BookingRequestDTO bookingRequestDTO = beanOutputConverter.convert(output);
        List<String> errors = new ArrayList<>();
        if(bookingRequestDTO.getCustomerName().isEmpty() || bookingRequestDTO.getCustomerName()== null){
            errors.add("Tên khách hàng không được để trống");
        }
        if(bookingRequestDTO.getCheckInDate() == null){
            errors.add("Ngày nhận phòng không được để trống");
        }
        if(bookingRequestDTO.getCheckOutDate() == null){
            errors.add("Ngày trả phòng không được để trống");
        }
        if(bookingRequestDTO.getNumberOfPeople() == null){
            errors.add("Số người không được để trống");
        }
        if(bookingRequestDTO.getBudget() == null || bookingRequestDTO.getBudget() < 1000000){
            errors.add("Ngân sách không được để trống và phải lớn hơn 1.000.000VND");
        }
        if(!errors.isEmpty()){
            throw new ConstraintViolationException(errors.stream().collect(Collectors.joining("\n")), Set.of());
        }
        memory.add(chatRequest.getMessage());

        return convertAndSaveBooking(bookingRequestDTO);
    }

    private String convertAndSaveBooking(ChatRequest chatRequest) {
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
    private String convertAndSaveBooking(@Valid BookingRequestDTO bookingRequestDTO){
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
            throw new RuntimeException(e.getMessage());
        }
    }
}
