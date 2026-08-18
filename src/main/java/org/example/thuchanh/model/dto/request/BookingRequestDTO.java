package org.example.thuchanh.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookingRequestDTO {
    @NotBlank(message = "Tên khách hàng không được để trống")
    @NotNull
    private String customerName;

    @NotNull(message = "Số người lớn đi cùng không được để trống")
    private Integer numberOfPeople;

    @NotNull(message = "Thời gian nhận phòng không được để trống")
    private LocalDateTime checkInDate;

    @NotNull(message = "Thời gian trả phòng không được để trống")
    private LocalDateTime checkOutDate;

    @NotNull(message = "Ngân sách không được để trống")
    @Min(value = 1000000, message = "Ngân sách không được nhỏ hơn 1.000.000VND")
    private Double budget;
}
