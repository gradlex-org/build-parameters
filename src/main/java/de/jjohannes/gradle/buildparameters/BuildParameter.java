package de.jjohannes.gradle.buildparameters;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;

public abstract class BuildParameter<ParameterType> {

    private final Identifier id;

    protected BuildParameter(Identifier identifier) {
        this.id = identifier;
    }

    @Input
    public String getSimpleName() {
        return id.lastSegment();
    }

    @Internal
    public String getPath() {
        return id.toDottedCase();
    }

    @Input
    @Optional
    public abstract Property<ParameterType> getDefaultValue();

    @Input
    @Optional
    public abstract Property<String> getDescription();
}
