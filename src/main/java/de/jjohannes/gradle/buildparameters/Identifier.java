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
