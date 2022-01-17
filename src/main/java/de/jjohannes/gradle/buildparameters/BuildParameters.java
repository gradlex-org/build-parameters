package de.jjohannes.gradle.buildparameters;

public @interface BuildParameters {
    String prefix() default "buildParameter";
}
