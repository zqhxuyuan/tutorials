package org.tguduru.guice.bind.annotation;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Guduru, Thirupathi Reddy
 */
@BindingAnnotation
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
// We can use this annotation instead of @Named for multiple implementation injections. Create multiple annotation like
// this for each @Named types.
public @interface LogServiceInject {
}
