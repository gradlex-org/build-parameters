// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import java.util.Locale;

final class Strings {

    static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1);
    }

    static String screamingSnakeCase(String str) {
        return str.toUpperCase(Locale.ROOT).replace(".", "_");
    }
}
