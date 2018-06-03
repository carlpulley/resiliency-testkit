package com.bamtech.resiliency.annotation;

import java.lang.annotation.*;

/**
 * Annotation indicating that resiliency fault agents should pass random values at a parameter position.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface RandomParameterValue {

    /**
     * Scalacheck generator that random parameter values will be sourced from.
     *
     * @return given the annotated parameter position has type {@code T}, the package path to a value of type
     *   {@code Gen[Option[T]]}
     */
    public String generator();
}
