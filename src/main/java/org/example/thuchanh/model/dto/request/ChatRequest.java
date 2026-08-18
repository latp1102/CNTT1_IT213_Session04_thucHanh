package org.example.thuchanh.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatRequest {
    @NotBlank(message = "Tin nhắn không được để trống")
    private String message;
}
