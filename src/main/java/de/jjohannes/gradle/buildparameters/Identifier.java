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

package de.jjohannes.gradle.buildparameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class Identifier {

    private final List<String> segments;

    static Identifier root() {
        return new Identifier(Collections.emptyList());
    }

    private Identifier(List<String> segments) {
        this.segments = Collections.unmodifiableList(segments);
    }

    Identifier append(String segment) {
        checkNotEmpty(segment);

        List<String> newSegments = new ArrayList<>(segments);
        newSegments.add(segment);
        return new Identifier(newSegments);
    }

    public String toCamelCase() {
        return segments.stream()
                .map(Strings::capitalize)
                .collect(Collectors.joining());
    }

    public String toDottedCase() {
        return String.join(".", segments);
    }

    private static void checkNotEmpty(String s) {
        if (s.trim().isEmpty()) {
            throw new IllegalArgumentException("Must not be empty.");
        }
    }

    public String lastSegment() {
        return segments.get(segments.size() - 1);
    }
}
