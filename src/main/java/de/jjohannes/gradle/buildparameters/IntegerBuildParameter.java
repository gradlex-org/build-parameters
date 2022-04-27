package de.jjohannes.gradle.buildparameters;

import javax.inject.Inject;

public abstract class IntegerBuildParameter extends BuildParameter<Integer> {

    @Inject
    public IntegerBuildParameter(Identifier identifier) {
        super(identifier);
    }

}
