package org.gradlex.buildparameters.fixture

import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation

class RestoreDefaultLocaleInterceptor implements IMethodInterceptor {
    public static final RestoreDefaultLocaleInterceptor INSTANCE = new RestoreDefaultLocaleInterceptor()

    private RestoreDefaultLocaleInterceptor() {}

    @Override
    void intercept(IMethodInvocation invocation) throws Throwable {
        Locale original = Locale.getDefault()

        try {
            invocation.proceed()
        } finally {
            Locale.setDefault(original)
        }
    }
}
