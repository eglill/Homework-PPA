package eglill.homework.exeption;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ValidationError {
    private String field;

    private String error;
}
