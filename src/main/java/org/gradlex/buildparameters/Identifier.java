/*
 * Copyright 2022 the GradleX team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
