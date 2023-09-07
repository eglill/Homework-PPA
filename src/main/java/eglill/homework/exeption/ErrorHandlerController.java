package eglill.homework.exeption;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationError handleValidationError(MissingServletRequestParameterException exception) {
        return ValidationError.
                builder().
                field(exception.getParameterName()).
                error(exception.getMessage()).
                build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationError handleValidationError(MethodArgumentTypeMismatchException exception) {
        return ValidationError.
                builder().
                field(exception.getName()).
                error(exception.getLocalizedMessage()).
                build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ValidationError> handleValidationError(ConstraintViolationException exception) {
        return buildValidationErrors(exception.getConstraintViolations());
    }

    private List<ValidationError>  buildValidationErrors(Set<ConstraintViolation<?>> violations) {
        return violations.stream().map(violation -> ValidationError.
                builder().
                field(Objects.requireNonNull(StreamSupport.stream(
                        violation.getPropertyPath().spliterator(), false).
                                reduce((first, second) -> second).
                                orElse(null)).
                                toString()).
                error(violation.getMessage()).
                build()
        ).collect(Collectors.toList());
    }
}
