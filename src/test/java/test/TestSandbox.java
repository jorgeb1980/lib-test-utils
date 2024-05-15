package test;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestSandbox {

    @Test
    public void testSandboxIsRemoved() {
        var sandbox = Sandbox.sandbox();
        sandbox.runTest((File directory) -> System.out.println(directory));
        assertFalse(sandbox.sandbox.exists());
    }

    @Test
    public void testOutputIsCaptured() {
        var sandbox = Sandbox.sandbox();
        var ctx = sandbox.runTest((File directory) -> System.out.println(directory.getAbsolutePath()), true);
        assertFalse(sandbox.sandbox.exists());
        assertEquals(sandbox.sandbox.getAbsolutePath(), ctx.out().trim());
    }

    @Test
    public void testErrorIsCaptured() {
        var sandbox = Sandbox.sandbox();
        var ctx = sandbox.runTest((File directory) -> System.err.println(directory.getAbsolutePath()), true);
        assertFalse(sandbox.sandbox.exists());
        assertEquals(sandbox.sandbox.getAbsolutePath(), ctx.err().trim());
    }

    @Test
    public void testNothingIsCapturedUnlessSpecified() {
        var sandbox = Sandbox.sandbox();
        var ctx = sandbox.runTest(
                (File directory) -> {
                    System.out.println(directory.getAbsolutePath());
                    System.err.println(directory.getAbsolutePath());
                },
            false
        );
        assertFalse(sandbox.sandbox.exists());
        assertNull(ctx.out());
        assertNull(ctx.err());
    }

    @Test
    public void testDefaultVersionOfMethodNoResult() {
        // Same behavior as if specifying not to capture
        var sandbox = Sandbox.sandbox();
        var ctx = sandbox.runTest(
            (File directory) -> {
                System.out.println(directory.getAbsolutePath());
                System.err.println(directory.getAbsolutePath());
            }
        );
        assertFalse(sandbox.sandbox.exists());
        assertNull(ctx.out());
        assertNull(ctx.err());
    }

    @Test
    public void testCaptureWithResult() {
        // Same behavior as if specifying not to capture
        var sandbox = Sandbox.sandbox();
        var ctx = sandbox.runTest(
            (File directory) -> {
                System.out.println("135");
                System.err.println("531");
                return 42;
            },
            true
        );
        assertFalse(sandbox.sandbox.exists());
        assertEquals(42, ctx.result());
        assertEquals("135", ctx.out().trim());
        assertEquals("531", ctx.err().trim());
    }

    @Test
    public void testNoCaptureWithResult() {
        // Same behavior as if specifying not to capture
        var sandbox = Sandbox.sandbox();
        var ctx = sandbox.runTest(
            (File directory) -> {
                System.out.println("135");
                System.err.println("531");
                return 42;
            },
            false
        );
        assertFalse(sandbox.sandbox.exists());
        assertEquals(42, ctx.result());
        assertNull(ctx.out());
        assertNull(ctx.err());
    }

    @Test
    public void testNoCaptureWithResultDefaultVersion() {
        // Same behavior as if specifying not to capture
        var sandbox = Sandbox.sandbox();
        var ctx = sandbox.runTest(
            (File directory) -> {
                System.out.println("135");
                System.err.println("531");
                return 42;
            }
        );
        assertFalse(sandbox.sandbox.exists());
        assertEquals(42, ctx.result());
        assertNull(ctx.out());
        assertNull(ctx.err());
    }
}
