package mk.mdt.budgetapi.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import mk.mdt.budgetapi.dto.ErrorResponse;
import mk.mdt.budgetapi.dto.ErrorResponse.FieldValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404
    @ExceptionHandler({ContractNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    // 409
    @ExceptionHandler({EmailAlreadyExistsException.class, ContractHasInvoicesException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
    }

    // 400
    @ExceptionHandler({InsufficientContractBalanceException.class, InvalidResetTokenException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
    }

    // 401
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
    }

    // 401
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJwt(JwtException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid or expired token.", req, null);
    }

    // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<FieldValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldValidationError(e.getField(), e.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed.", req, errors);
    }

    // 400
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleMalformed(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "Malformed request.", req, null);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error on {}", req.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred.", req, null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                HttpServletRequest req,
                                                List<FieldValidationError> fieldErrors) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                message, req.getRequestURI(), fieldErrors);
        return ResponseEntity.status(status).body(body);
    }
}