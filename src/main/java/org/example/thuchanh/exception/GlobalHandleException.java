package org.example.thuchanh.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GlobalHandleException {
    private final ChatClient chatClient;

    public GlobalHandleException(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        String prompt = """
                Dưới đây là thông tin các lỗi mà người dùng nhập thiếu khi đặt phòng, 
                bạn hãy format lại và trả lời khách hàng nhé :
                %s
                """.formatted(errors.stream().collect(Collectors.joining("\n")));
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        String prompt = """
                Dưới đây là thông tin các lỗi mà người dùng nhập thiếu khi đặt phòng, 
                bạn hãy format lại và trả lời khách hàng nhé :
                %s
                """.formatted(e.getMessage());
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
