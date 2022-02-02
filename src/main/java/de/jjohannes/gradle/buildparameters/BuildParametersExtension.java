package de.jjohannes.gradle.buildparameters;


import org.gradle.api.NamedDomainObjectContainer;

public interface BuildParametersExtension {

    NamedDomainObjectContainer<BuildParameter> getParameters();

}
