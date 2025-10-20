// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Identifier {

    private final List<String> segments;

    Identifier(List<String> segments) {
        this.segments = Collections.unmodifiableList(segments);
    }

    Identifier append(String segment) {
        checkNotEmpty(segment);

        List<String> newSegments = new ArrayList<>(segments);
        newSegments.add(segment);
        return new Identifier(newSegments);
    }

    public String toPropertyPath() {
        return String.join(".", segments);
    }

    public String toPackageFolderPath() {
        return Constants.PACKAGE_NAME + "/" + String.join("/", segments.subList(0, segments.size() - 1));
    }

    public String toPackageName() {
        List<String> packageSegments = segments.subList(0, segments.size() - 1);
        return Constants.PACKAGE_NAME + (packageSegments.isEmpty() ? "" : ".") + String.join(".", packageSegments);
    }

    public String toSimpleTypeName() {
        return Strings.capitalize(toFieldName());
    }

    public String toFieldName() {
        return segments.get(segments.size() - 1);
    }

    public String toFullQualifiedTypeName() {
        return toPackageName() + "." + toSimpleTypeName();
    }

    public String toEnvironmentVariableName() {
        return Strings.screamingSnakeCase(toPropertyPath());
    }

    private static void checkNotEmpty(String s) {
        if (s.trim().isEmpty()) {
            throw new IllegalArgumentException("Must not be empty.");
        }
    }
}
