package com.bamtech.resiliency.annotation;

import java.lang.annotation.*;

/**
 * Annotation indicating that resiliency fault agents should return random values.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RandomReturnValue {

    /**
     * Scalacheck generator that random return values will be sourced from.
     *
     * @return given the annotated method has return type {@code T}, the package path to a value of type
     *   {@code Gen[Option[T]]}
     */
    public String generator();
}
