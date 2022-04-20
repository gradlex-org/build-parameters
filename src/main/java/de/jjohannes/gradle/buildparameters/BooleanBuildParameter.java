package de.jjohannes.gradle.buildparameters;

import javax.inject.Inject;

public abstract class BooleanBuildParameter extends BuildParameter<Boolean> {

    @Inject
    public BooleanBuildParameter(String name, String prefix) {
        super(name, prefix);
    }

}
