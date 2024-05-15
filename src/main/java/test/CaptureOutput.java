package test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.fail;

public class CaptureOutput {

    @FunctionalInterface
    public interface CouldThrowSomething {
        Object run() throws Exception;
    }

    @FunctionalInterface
    public interface CouldThrowSomethingVoid {
        void run() throws Exception;
    }

    /**
     * Will run the lambda passed as parameter while replacing System.out and System.err, in order to capture
     * whatever the method outputs.  NON THREAD SAFE
     * @param action Lambda that we need to test, in this case one that does not return anything
     * @return Execution context consisting of
     *      - whatever the action returned - null
     *      - if it was capturing, the standard output
     *      - if it was capturing, the error output
     */
    public static ExecutionContext captureOutput(CouldThrowSomethingVoid action) {
        return captureOutput(() -> { action.run(); return null; });
    }

    /**
     * Will run the lambda passed as parameter while replacing System.out and System.err, in order to capture
     * whatever the method outputs.  NON THREAD SAFE
     * @param action Lambda that we need to test
     * @return Execution context consisting of
     *      - whatever the action returned
     *      - if it was capturing, the standard output
     *      - if it was capturing, the error output
     */
    public static ExecutionContext captureOutput(CouldThrowSomething action) {
        final var myOut = new ByteArrayOutputStream();
        final var myErr = new ByteArrayOutputStream();
        final PrintStream originalStdOut = System.out;
        final PrintStream originalErr = System.err;
        System.setOut(new PrintStream(myOut));
        System.setErr(new PrintStream(myErr));
        String standardOutput = "";
        String errOutput = "";
        Object ret = null;
        try {
            ret = action.run();
        } catch (Exception e) {
            fail(e);
        } finally {
            standardOutput = myOut.toString();
            errOutput = myErr.toString();
            System.setOut(originalStdOut);;
            System.setErr(originalErr);
        }
        return new ExecutionContext(ret, standardOutput, errOutput);
    }
}
