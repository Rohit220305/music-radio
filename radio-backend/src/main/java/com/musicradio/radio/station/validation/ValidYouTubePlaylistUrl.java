package com.musicradio.radio.station.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a string is a valid HTTPS YouTube playlist URL containing a valid playlist ID in the 'list' query parameter.
 */
@Documented
@Constraint(validatedBy = YouTubePlaylistUrlValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYouTubePlaylistUrl {

    String message() default "Must be a valid HTTPS YouTube playlist URL with a valid 'list' query parameter";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

