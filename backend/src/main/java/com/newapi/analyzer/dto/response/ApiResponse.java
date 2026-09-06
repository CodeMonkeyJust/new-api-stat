package com.newapi.analyzer.dto.response;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return response(200, "success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return response(code, message, null);
    }

    private static <T> ApiResponse<T> response(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
