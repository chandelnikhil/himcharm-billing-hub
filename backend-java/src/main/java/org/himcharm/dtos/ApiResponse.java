package org.himcharm.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard envelope returned by every endpoint / exception handler.
 * Null fields (e.g. {@code data} on errors, {@code errors} on success) are omitted from the JSON.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private boolean success;
    private int status;
    private String message;
    private Object data;

    /** Field-level or detail errors (e.g. validation failures). */
    private Object errors;

    /** Request URI that produced this response. */
    private String path;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static ApiResponse success(int status, String message, Object data) {
        return ApiResponse.builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse error(int status, String message, Object errors, String path) {
        return ApiResponse.builder()
                .success(false)
                .status(status)
                .message(message)
                .errors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
