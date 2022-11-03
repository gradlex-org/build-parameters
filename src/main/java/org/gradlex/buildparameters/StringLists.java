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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class StringLists {

    private StringLists() {
    }

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
