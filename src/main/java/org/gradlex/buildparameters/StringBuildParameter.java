// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import javax.inject.Inject;

public abstract class StringBuildParameter extends BuildParameter<String> {

    @Inject
    public StringBuildParameter(Identifier identifier) {
        super(identifier);
    }
}
