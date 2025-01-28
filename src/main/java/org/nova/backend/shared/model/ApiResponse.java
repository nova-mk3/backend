package org.nova.backend.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "success", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(HttpStatus.CREATED.value(), "success created", data);
    }

    public static ApiResponse<Void> noContent() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "success noContent", null);
    }

    public static ApiResponse<Void> error(String message, int status) {
        return new ApiResponse<>(status, message, null);
    }
}
