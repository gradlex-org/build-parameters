// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import javax.inject.Inject;

public abstract class IntegerBuildParameter extends BuildParameter<Integer> {

    @Inject
    public IntegerBuildParameter(Identifier identifier) {
        super(identifier);
    }
}
