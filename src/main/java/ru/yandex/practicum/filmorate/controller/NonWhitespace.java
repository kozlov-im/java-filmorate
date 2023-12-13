package ru.yandex.practicum.filmorate.controller;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NonWhitespaceValidator.class)
public @interface NonWhitespace {
    String message() default "there are whitespaces exist";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
