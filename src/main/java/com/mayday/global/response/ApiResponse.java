package com.mayday.global.response;

public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T data;

    private ApiResponse(int status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(String message, T data){
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> created(String message, T data){
        return new ApiResponse<>(201, message, data);
    }

    public static <T> ApiResponse<T> error(int status, String message, T data){
        return new ApiResponse<>(status, message, data);
    }

    public int getStatus(){
        return status;
    }

    public String getMessage(){
        return message;
    }

    public T getData(){
        return data;
    }
}
