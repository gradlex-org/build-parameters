// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import java.util.stream.Stream;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.Optional;

public abstract class BuildParameterGroup {

    private static final Action<Object> NO_OP = o -> {};

    final Identifier id;

    @Inject
    public BuildParameterGroup(Identifier identifier) {
        this.id = identifier;
    }

    /**
     * @since 1.3
     */
    @Input
    @Optional
    public abstract Property<String> getDescription();

    /**
     * @since 1.2
     */
    public void string(String name) {
        configureParameter(name, StringBuildParameter.class, NO_OP);
    }

    public void string(String name, Action<? super BuildParameter<String>> configure) {
        configureParameter(name, StringBuildParameter.class, configure);
    }

    /**
     * @since 1.2
     */
    public void integer(String name) {
        configureParameter(name, IntegerBuildParameter.class, NO_OP);
    }

    public void integer(String name, Action<? super BuildParameter<Integer>> configure) {
        configureParameter(name, IntegerBuildParameter.class, configure);
    }

    /**
     * @since 1.2
     */
    public void bool(String name) {
        configureParameter(name, BooleanBuildParameter.class, NO_OP);
    }

    public void bool(String name, Action<? super BuildParameter<Boolean>> configure) {
        configureParameter(name, BooleanBuildParameter.class, configure);
    }

    public void enumeration(String name, Action<EnumBuildParameter> configure) {
        configureParameter(name, EnumBuildParameter.class, configure);
    }

    private <T extends BuildParameter<?>> void configureParameter(
            String name, Class<T> paramType, Action<? super T> configure) {
        T parameter = getObjects().newInstance(paramType, id.append(name));
        parameter.getMandatory().convention(false);
        configure.execute(parameter);
        getParameters().add(parameter);
    }

    public void group(String name, Action<? super BuildParameterGroup> configure) {
        BuildParameterGroup group = getObjects().newInstance(BuildParameterGroup.class, id.append(name));
        configure.execute(group);
        getGroups().add(group);
    }

    @Inject
    protected abstract ObjectFactory getObjects();

    @Nested
    public abstract ListProperty<BuildParameter<?>> getParameters();

    @Nested
    public abstract ListProperty<BuildParameterGroup> getGroups();

    @Input
    public String getPropertyPath() {
        return id.toPropertyPath();
    }

    java.util.Optional<BuildParameter<?>> findParameter(String propertyPath) {
        java.util.Optional<BuildParameter<?>> match = getParameters().get().stream()
                .filter(p -> p.getPropertyPath().equals(propertyPath))
                .findFirst();
        if (match.isPresent()) {
            return match;
        } else {
            return getGroups().get().stream()
                    .flatMap(g -> stream(g.findParameter(propertyPath)))
                    .findFirst();
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static <T> Stream<T> stream(java.util.Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }
}
