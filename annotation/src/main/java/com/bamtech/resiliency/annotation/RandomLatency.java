// Copyright 2018 Carl Pulley

package com.bamtech.resiliency.annotation;

import java.lang.annotation.*;

/**
 * Annotation indicating that resiliency fault agents should add random latency to a method call.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface RandomLatency {

    /**
     * Scalacheck generator that random finite durations will be sourced from.
     *
     * @return package path to a value of type {@code Gen[Option[FiniteDuration]]}
     */
    public String generator();
}
