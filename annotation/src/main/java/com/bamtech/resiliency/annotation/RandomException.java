// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.annotation;

import java.lang.annotation.*;

/**
 * Annotation indicating that resiliency fault agents should throw random exceptions.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface RandomException {

    /**
     * Scalacheck generator that random exceptions will be sourced from.
     *
     * @return package path to a value of type {@code Gen[Option[Throwable]]}
     */
    public String generator();
}
