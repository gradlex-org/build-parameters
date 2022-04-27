package de.jjohannes.gradle.buildparameters;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;

import javax.inject.Inject;

public abstract class EnumBuildParameter extends BuildParameter<String> {

    @Input
    public abstract ListProperty<String> getValues();

    @Inject
    public EnumBuildParameter(Identifier identifier) {
        super(identifier);
    }

}
