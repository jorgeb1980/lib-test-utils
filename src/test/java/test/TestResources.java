package test;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static test.Sandbox.sandbox;

public class TestResources {

    @Test
    public void testCopyResources() {
        var sb = sandbox();
        sb.runTest((File sandbox) -> {
            var resource = sb.copyResource("/testResource.txt");
            assertEquals(sandbox, resource.getParentFile());
            assertEquals("testResource", Files.readString(resource.toPath()));
        });
    }

    @Test
    public void testCopyResourcesWithHierarchy() {
        var sb = sandbox();
        sb.runTest((File sandbox) -> {
            var resource = sb.copyResource("/childrenDirectory/otherResource.txt");
            assertNotEquals(sandbox, resource.getParentFile());
            assertEquals(new File(sandbox, "childrenDirectory"), resource.getParentFile());
            assertEquals("otherResource", Files.readString(resource.toPath()));
        });
    }

    @Test
    public void testCopyResourcesToCustomizedPath() {
        var sb = sandbox();
        sb.runTest((File sandbox) -> {
            var resource1 = sb.copyResource("/childrenDirectory/otherResource.txt", "whatever/someFile.txt");
            assertNotEquals(sandbox, resource1.getParentFile());
            assertEquals(new File(sandbox, "whatever"), resource1.getParentFile());
            assertEquals("someFile.txt", resource1.getName());
            assertEquals("otherResource", Files.readString(resource1.toPath()));

            var resource2 = sb.copyResource("/testResource.txt", "lololol/lol.txt");
            assertNotEquals(sandbox, resource2.getParentFile());
            assertEquals(new File(sandbox, "lololol"), resource2.getParentFile());
            assertEquals("lol.txt", resource2.getName());
            assertEquals("testResource", Files.readString(resource2.toPath()));
        });
    }
}
