package test.sandbox;

import org.junit.jupiter.api.Assertions;
import test.Sandbox;

public class TestSandboxProvider {

    @SandboxTest
    public void testSandbox(Sandbox sb) {
        var rootDirectory = sb.getSandbox();
        Assertions.assertNotNull(rootDirectory);
        Assertions.assertTrue(rootDirectory.isDirectory());
    }
}
