package test.sandbox;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import test.Sandbox;

public class SandboxProvider implements AfterTestExecutionCallback, ParameterResolver {

    private final static Namespace NAMESPACE =
        Namespace.create(SandboxProvider.class);
    private final static String KEY = "sandbox";

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == Sandbox.class;
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        var sb = Sandbox.sandbox();
        extensionContext.getStore(NAMESPACE).put(KEY, sb);
        return sb;
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        var sandbox = (Sandbox) extensionContext.getStore(NAMESPACE).get(KEY);
        if (sandbox != null) sandbox.cleanup();
    }
}
