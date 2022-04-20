package de.jjohannes.gradle.buildparameters;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;

import javax.inject.Inject;

public abstract class BuildParameterGroup {

    static final String BASE_GROUP_NAME = "buildParametersExtension";

    private final String name;
    private final String prefix;

    @Inject
    public BuildParameterGroup(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    public void string(String name, Action<? super BuildParameter<String>> configure) {
        String parameterPrefix = getPrefix();
        BuildParameter<String> parameter = getObjects().newInstance(StringBuildParameter.class, name, parameterPrefix);
        configure.execute(parameter);
        getParameters().add(parameter);
    }

    public void integer(String name, Action<? super BuildParameter<Integer>> configure) {
        String parameterPrefix = getPrefix();
        BuildParameter<Integer> parameter = getObjects().newInstance(IntegerBuildParameter.class, name, parameterPrefix);
        configure.execute(parameter);
        getParameters().add(parameter);
    }

    public void bool(String name, Action<? super BuildParameter<Boolean>> configure) {
        String parameterPrefix = getPrefix();
        BuildParameter<Boolean> parameter = getObjects().newInstance(BooleanBuildParameter.class, name, parameterPrefix);
        configure.execute(parameter);
        getParameters().add(parameter);
    }

    public void group(String name, Action<? super BuildParameterGroup> configure) {
        String groupPrefix = getPrefix();
        BuildParameterGroup group = getObjects().newInstance(BuildParameterGroup.class, name, groupPrefix);
        configure.execute(group);
        getGroups().add(group);
    }

    private String getPrefix() {
        if (isBaseGroup()) {
            return "";
        } else if (prefix.isEmpty()) {
            return name;
        } else {
            return prefix + "." + this.name;
        }
    }

    @Inject
    protected abstract ObjectFactory getObjects();

    @Nested
    public abstract ListProperty<BuildParameter<?>> getParameters();

    @Nested
    public abstract ListProperty<BuildParameterGroup> getGroups();

    @Input
    public String getName() {
        return name;
    }

    private boolean isBaseGroup() {
        return BASE_GROUP_NAME.equals(name);
    }
}
