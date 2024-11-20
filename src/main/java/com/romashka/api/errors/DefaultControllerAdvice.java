package com.romashka.api.errors;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class DefaultControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> Objects.requireNonNullElse(f.getDefaultMessage(), "")));
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setInstance(ex.getBody().getInstance());
        problemDetail.setTitle("Validation failed");
        problemDetail.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ProblemDetail handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        Map<String, String> errors = ex.getAllValidationResults().stream()
                .collect(Collectors.toMap(
                                r -> r.getMethodParameter()
                                        .getParameter()
                                        .getName(),
                                r -> r
                                        .getResolvableErrors()
                                        .stream()
                                        .map(MessageSourceResolvable::getDefaultMessage)
                                        .collect(Collectors.joining(". "))
                        )
                );
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setInstance(ex.getBody().getInstance());
        problemDetail.setTitle("Validation failed");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ApiResponses({
            @ApiResponse(
                    responseCode = "100-511",
                    description = "Problem details", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(ex.getStatusCode());
        problemDetail.setInstance(ex.getBody().getInstance());
        problemDetail.setTitle(ex.getStatusCode().toString());
        problemDetail.setDetail(ex.getReason());
        return new ResponseEntity<>(problemDetail, ex.getStatusCode());
    }
}
