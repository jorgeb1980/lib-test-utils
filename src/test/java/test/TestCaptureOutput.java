package test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestCaptureOutput {

    @Test
    public void testNoReturnValue() {
        var ctx = CaptureOutput.captureOutput(() -> {
            System.out.println("135");
            System.err.println("531");
        });
        assertNull(ctx.result());
        assertEquals("135", ctx.out().trim());
        assertEquals("531", ctx.err().trim());
    }

    @Test
    public void testWithReturnValue() {
        var ctx = CaptureOutput.captureOutput(() -> {
            System.out.println("135");
            System.err.println("531");
            return 444;
        });
        assertEquals(444, ctx.result());
        assertEquals("135", ctx.out().trim());
        assertEquals("531", ctx.err().trim());
    }
}
