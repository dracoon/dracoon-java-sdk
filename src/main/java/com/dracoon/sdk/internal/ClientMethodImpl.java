package com.dracoon.sdk.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ClientMethodImpl {
    String value() default "";
}
