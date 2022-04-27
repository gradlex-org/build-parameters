package de.jjohannes.gradle.buildparameters;

import javax.inject.Inject;

public abstract class StringBuildParameter extends BuildParameter<String> {

    @Inject
    public StringBuildParameter(Identifier identifier) {
        super(identifier);
    }

}
