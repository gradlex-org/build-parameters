// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class StringLists {

    private StringLists() {}

    static void dropLeadingAndTrailingEmptyLines(List<String> input) {
        dropLeadingEmptyLines(input);
        dropTrailingEmptyLines(input);
    }

    static void dropLeadingEmptyLines(List<String> input) {
        Iterator<String> it = input.iterator();
        boolean nonEmptyLineReached = false;
        while (it.hasNext() && !nonEmptyLineReached) {
            String line = it.next();
            if (line.isEmpty()) {
                it.remove();
            } else {
                nonEmptyLineReached = true;
            }
        }
    }

    public static void dropTrailingEmptyLines(List<String> input) {
        ListIterator<String> it = input.listIterator(input.size());
        boolean nonEmptyLineReached = false;
        while (it.hasPrevious() && !nonEmptyLineReached) {
            String line = it.previous();
            if (line.isEmpty()) {
                it.remove();
            } else {
                nonEmptyLineReached = true;
            }
        }
    }
}
