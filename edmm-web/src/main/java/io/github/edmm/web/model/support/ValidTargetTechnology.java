package io.github.edmm.web.model.support;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = TargetTechnologyValidator.class)
public @interface ValidTargetTechnology {

    String message() default "must be a valid target technology";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
