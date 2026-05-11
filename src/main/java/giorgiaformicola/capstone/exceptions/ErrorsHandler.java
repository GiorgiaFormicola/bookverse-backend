package giorgiaformicola.capstone.exceptions;

import giorgiaformicola.capstone.payloads.ErrorDTO;
import giorgiaformicola.capstone.payloads.ErrorsListDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorsHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleBadRequestException(BadRequestException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(PayloadValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsListDTO handlePayloadValidationException(PayloadValidationException ex) {
        return new ErrorsListDTO(ex.getMessage(), LocalDateTime.now(), ex.getErrors());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleUnauthorizedException(UnauthorizedException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFoundException(NotFoundException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        return new ErrorDTO("Access denied, you don't have the required permission", LocalDateTime.now());
    }

    /*FOR INVALID UUID IN THE PATH VARIABLE*/
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return new ErrorDTO("Oops, something went wrong with you request", LocalDateTime.now());
    }


    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleValidationException(ValidationException ex) {
        return new ErrorDTO(ex.getMessage(), LocalDateTime.now());
    }


    //TODO: handle missing errors
}
