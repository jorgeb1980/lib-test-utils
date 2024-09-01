package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class Sandbox {

    private final static AtomicInteger testCounter = new AtomicInteger(1);
    final File sandbox = createTempDirectory();

    public File getSandbox() { return sandbox; }

    private Sandbox() {
    }

    public static Sandbox sandbox() {
        return new Sandbox();
    }

    @FunctionalInterface
    public interface RunnableInTempDirectory {
        Object run(File directory) throws Exception;
    }

    @FunctionalInterface
    public interface RunnableInTempDirectoryVoid {
        void run(File directory) throws Exception;
    }

    /**
     * Will run the specified lambda in a temporal directory.  This temporal directory is created and disposed of
     * in a thread-safe manner.
     * @param action Lambda running on the temporal directory
     * @return Execution context consisting of
     *  - whatever the action returned
     *  - no standard output
     *  - no error output
     */
    public ExecutionContext runTest(RunnableInTempDirectory action) {
        return runTest(action, false);
    }

    /**
     * Will run the specified lambda in a temporal directory.  This temporal directory is created and disposed of
     * in a thread-safe manner.
     * @param action Lambda running on the temporal directory, in this case one that does not return anything
     * @return Execution context consisting of
     *  - whatever the action returned
     *  - no standard output
     *  - no error output
     */
    public ExecutionContext runTest(RunnableInTempDirectoryVoid action) {
        return runTest(action, false);
    }

    // Creates a temporal sandbox in which we will run tests
    private static File createTempDirectory() {
        File ret;
        try {
            ret = Files.createTempDirectory("tmp" + testCounter.addAndGet(1)).toFile();
        } catch(IOException ioe) {
            ret = null;
            fail(ioe);
        }
        return ret;
    }

    /**
     * Will run the specified lambda in a temporal directory.  This temporal directory is created and disposed of
     * in a thread-safe manner.  This version of the method however becomes NOT thread-safe if instructed to capture
     * outputs of the lambda.
     * @param action Lambda running on the temporal directory
     * @param captureOutput If true, the method will try to capture stdOut and error output in a non-thread-safe manner
     * @return Execution context consisting of
     *  - whatever the action returned
     *  - if it was capturing, the standard output
     *  - if it was capturing, the error output
     */
    public ExecutionContext runTest(RunnableInTempDirectoryVoid action, Boolean captureOutput) {
        return runTest((File dir) -> { action.run(sandbox); return null; }, captureOutput);
    }

    /**
     * Will run the specified lambda in a temporal directory.  This temporal directory is created and disposed of
     * in a thread-safe manner.  This version of the method however becomes NOT thread-safe if instructed to capture
     * outputs of the lambda.
     * @param action Lambda running on the temporal directory
     * @param captureOutput If true, the method will try to capture stdOut and error output in a non-thread-safe manner
     * @return Execution context consisting of
     *  - whatever the action returned
     *  - if it was capturing, the standard output
     *  - if it was capturing, the error output
     */
    public ExecutionContext runTest(RunnableInTempDirectory action, Boolean captureOutput) {
        ExecutionContext ret = null;
        try {
            if (captureOutput) ret = CaptureOutput.captureOutput(() -> action.run(sandbox));
            else ret = new ExecutionContext(action.run(sandbox), null, null);
        } catch (Exception e) { fail(e); }
        finally {
            try {
                // Delete recursively
                if (sandbox != null) removeDirectory(sandbox);
            } catch (IOException ioe) {
                // Fail too if cleanup was not possible for whatever reason
                fail(ioe);
            }
        }
        return ret;
    }

    // Converts a classpath string into a string that will be used to create a children file inside the sandbox
    private String extractPath(String classpath) {
        if (classpath.startsWith("/")) return classpath.substring(1);
        else return classpath;
    }

    // Will delete the sandbox and whatever is sitting there
    private void removeDirectory(File directory) throws IOException {
        assert directory != null;
        assert !Files.isSymbolicLink(directory.toPath());
        assert directory.isDirectory();

        Files.walk(directory.toPath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    public File copyResource(String resourcePath) {
        return copyResource(resourcePath, null);
    }

    // Makes it sure a string used to find a resource in the classpath starts with '/'
    private String validateClasspathPath(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    /**
     * Copies bit to bit some resource from the classpath into the sandboxed directory
     * @param resourcePath Valid classloader path to an existing resource in the classpath (e.g., something in the
     *                     src/test/resources directory)
     * @param newPath Full path inside the sandbox where the resource will be copied on.  If null, the resource will be
     *                copied in the same path it had in the classpath
     * @return File object that references the resource created in the filesystem.
     */
    public File copyResource(String resourcePath, String newPath) {
        File toFile = new File(sandbox, newPath != null ? extractPath(newPath) : extractPath(resourcePath));
        assertFalse(toFile.exists());
        if (!toFile.getParentFile().equals(sandbox)) toFile.getParentFile().mkdirs();
        try (var is = Sandbox.class.getResourceAsStream(validateClasspathPath(resourcePath))) {
            assertNotNull(is);
            Files.copy(is, toFile.toPath());
        } catch (IOException ioe) { fail(ioe); }
        return toFile;
    }


}
