package com.restful.restaurant.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Standard response wrapper")
@Data
public class CommonResponse<T> {

    private T data;
    private ErrorResponse error;

    public CommonResponse(T data) {
        this.data = data;
        this.error = null;
    }

    public CommonResponse(ErrorResponse error) {
        this.error = error;
    }

    @Schema(description = "Error details")
    @Data
    public static class ErrorResponse {
        private String exception;
        private String message;

        public ErrorResponse(String exception, String message) {
            this.exception = exception;
            this.message = message;
        }

    }

}
