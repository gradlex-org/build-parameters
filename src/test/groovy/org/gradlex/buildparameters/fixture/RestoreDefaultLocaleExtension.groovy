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

package org.gradlex.buildparameters.fixture

import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.builtin.RestoreSystemPropertiesInterceptor
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import org.spockframework.runtime.model.parallel.ExclusiveResource
import org.spockframework.runtime.model.parallel.ResourceAccessMode
import org.spockframework.runtime.model.parallel.Resources

class RestoreDefaultLocaleExtension implements IAnnotationDrivenExtension<RestoreDefaultLocale> {

    private static final ExclusiveResource EXCLUSIVE_RESOURCE = new ExclusiveResource(Resources.LOCALE,
            ResourceAccessMode.READ_WRITE)

    @Override
    void visitSpecAnnotation(RestoreDefaultLocale annotation, SpecInfo spec) {
        spec.addExclusiveResource(EXCLUSIVE_RESOURCE)
        for (FeatureInfo feature : spec.getFeatures()) {
            feature.addInterceptor(RestoreDefaultLocaleInterceptor.INSTANCE)
        }
    }

    @Override
    void visitFeatureAnnotation(RestoreDefaultLocale annotation, FeatureInfo feature) {
        feature.addInterceptor(RestoreDefaultLocaleInterceptor.INSTANCE)
        feature.addExclusiveResource(EXCLUSIVE_RESOURCE)
    }


}
