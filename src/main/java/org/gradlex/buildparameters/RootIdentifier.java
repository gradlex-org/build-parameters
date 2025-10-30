// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import java.util.Collections;

class RootIdentifier extends Identifier {

    RootIdentifier() {
        super(Collections.emptyList());
    }

    @Override
    public String toPropertyPath() {
        return "";
    }

    @Override
    public String toPackageFolderPath() {
        return Constants.PACKAGE_NAME;
    }

    @Override
    public String toPackageName() {
        return Constants.PACKAGE_NAME;
    }

    @Override
    public String toSimpleTypeName() {
        return "BuildParametersExtension";
    }

    @Override
    public String toFieldName() {
        return "buildParametersExtension";
    }
}
