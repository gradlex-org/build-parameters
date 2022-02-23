package de.jjohannes.gradle.buildparameters;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

public abstract class BuildParametersExtension {

    private final ObjectFactory objects;

    @Inject
    public BuildParametersExtension(ObjectFactory objects) {
        this.objects = objects;
    }

    public void parameter(String name, Action<? super BuildParameter> configure) {
        BuildParameter parameter = objects.newInstance(BuildParameter.class, name);
        configure.execute(parameter);
        getParameters().add(parameter);
    }

    public abstract ListProperty<BuildParameter> getParameters();
}
