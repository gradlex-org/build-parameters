package de.jjohannes.gradle.buildparameters;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

import javax.inject.Inject;

public abstract class BuildParameter {

    private final String name;

    @Inject
    public BuildParameter(String name) {
        this.name = name;
    }

    @Input
    public String getName() {
        return name;
    }

    @Input
    @Optional
    public abstract Property<String> getDefaultValue();

    @Input
    public abstract Property<String> getDescription();
}
