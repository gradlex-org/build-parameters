package de.jjohannes.gradle.buildparameters;

public @interface BuildParameter {
    String defaultValue() default ""; // TODO what about optional values?
    String description() default "";
}
