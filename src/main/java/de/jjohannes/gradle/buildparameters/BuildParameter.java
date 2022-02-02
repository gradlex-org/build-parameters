package de.jjohannes.gradle.buildparameters;

import org.gradle.api.provider.Property;

public abstract class BuildParameter {

    private final String name;

    public BuildParameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Property<String> getDefaultValue();
    public abstract Property<String> getDescription();
}
