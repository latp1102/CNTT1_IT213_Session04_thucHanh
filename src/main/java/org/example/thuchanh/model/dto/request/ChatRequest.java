package org.example.thuchanh.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChatRequest {
    @NotBlank(message = "Tin nhắn không được để trống")
    private String message;
}
