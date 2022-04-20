package de.jjohannes.gradle.buildparameters;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;

import javax.inject.Inject;

public abstract class BuildParameter {

    private final String name;
    private final String prefix;

    @Inject
    public BuildParameter(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    @Input
    public String getName() {
        return name;
    }

    @Internal
    public String getPath() {
        return prefix.isEmpty() ? name : prefix + "." + name;
    }

    @Input
    @Optional
    public abstract Property<String> getDefaultValue();

    @Input
    @Optional
    public abstract Property<String> getDescription();
}
