// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import javax.inject.Inject;

public abstract class BooleanBuildParameter extends BuildParameter<Boolean> {

    @Inject
    public BooleanBuildParameter(Identifier identifier) {
        super(identifier);
    }
}
