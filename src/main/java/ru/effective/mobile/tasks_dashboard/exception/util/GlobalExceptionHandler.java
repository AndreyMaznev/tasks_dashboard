package ru.effective.mobile.tasks_dashboard.exception.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.effective.mobile.tasks_dashboard.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity <ApplicationError> handleUserNotFoundException (UserNotFoundException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.NOT_FOUND.value(),
                        e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    //for spring security
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity <ApplicationError> handleUsernameNotFoundException (UsernameNotFoundException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.NOT_FOUND.value(),
                        e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyHaveAssignedRoleException.class)
    public ResponseEntity <ApplicationError> handleUserAlreadyHaveAssignedRoleException (UserAlreadyHaveAssignedRoleException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.CONFLICT.value(),
                        e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity <ApplicationError> handleEmailAlreadyInUseException (EmailAlreadyInUseException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.CONFLICT.value(),
                        e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalCredentialsException.class)
    public ResponseEntity <ApplicationError> handleIllegalCredentialsException (IllegalCredentialsException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalJwtTokenException.class)
    public ResponseEntity <ApplicationError> handleIllegalJwtTokenException (IllegalJwtTokenException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessRefusedException.class)
    public ResponseEntity <ApplicationError> handleAccessRefusedException (AccessRefusedException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.FORBIDDEN.value(),
                        e.getMessage()),
                HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(TaskUpdateException.class)
    public ResponseEntity <ApplicationError> handleTaskUpdateException (TaskUpdateException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity <ApplicationError> handleTaskNotFoundException (TaskNotFoundException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.NOT_FOUND.value(),
                        e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalTaskStatusException.class)
    public ResponseEntity <ApplicationError> handleIllegalTaskStatusException (IllegalTaskStatusException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalTaskPriorityException.class)
    public ResponseEntity <ApplicationError> handleIllegalTaskPriorityException (IllegalTaskPriorityException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity <ApplicationError> handleCommentNotFoundException (CommentNotFoundException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.NOT_FOUND.value(),
                        e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CommentUpdateException.class)
    public ResponseEntity <ApplicationError> handleCommentNotFoundException (CommentUpdateException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity <ApplicationError> catchGeneralException(Exception e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity <ApplicationError> catchHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error (e.getMessage(), e);
        return new ResponseEntity <> (
                new ApplicationError(HttpStatus.BAD_REQUEST.value(),
                        "Некорректный JSON!"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ValidationErrorResponse response = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибка валидации",
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //    //todo читаемо норм?
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach(error -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//    }
}
