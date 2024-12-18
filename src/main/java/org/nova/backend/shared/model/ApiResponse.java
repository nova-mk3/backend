package org.nova.backend.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    public static ApiResponse<Void> error(String message, int status) {
        return new ApiResponse<>(status, message, null);
    }
}
