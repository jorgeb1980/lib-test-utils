# lib-test-utils

Provides utility classes and functions that can be used to test software
in sandboxed directories and leave only the desired trace

## Use cases

### JUnit 5 extension

You can declare a `@SandboxTest` with a `Sandbox` parameter for a JUnit test that 
will run inside a temporal directory (the sandbox), that is assured to be removed
after the execution.

The `Sandbox` object also allows easily to import resources into the temporal directory,
such as those in `src/test/resources`, as well as creating them as necessary from strings, 
bytes, etc.

```java
@TestSandbox
public void test(Sandbox sb) {
    // Bring src/test/resources/someDir/someFile.txt to the sandbox
    File someFile = sb.copyResource("someDir/someFile.txt");
    // Bring it again, with another name and path
    File someFileAgain = sb.copyResource(
        "someDir/someFile.txt",
        "anotherPlace/anotherFile.txt"
    );
    // Create some file
    File f = sb.createResource(
        "wherever/whatever",
        "some content",
        Charset.forName("UTF-8")
    );
    // Do your tests
}
```

### I cannot use the JUnit extension

There are many possible cases in which I am not interested in using `@TestSandbox`
- I am not using JUnit
- I want to have a `@ParameterizedTest`, with which it is not compatible

So we can add some boilerplate code to each test in order to have the
same functionality.

```java
@ParameterizedTest
@EnumSource(SomeEnum.class)
public void testForEnum(SomeEnum testParam) {
    var sb = Sandbox.sandbox();
    sb.runTest((File directory) -> {
        // Bring src/test/resources/someDir/someFile.txt to the sandbox
        File someFile = sb.copyResource("someDir/someFile.txt");
        // Bring it again, with another name and path
        File someFileAgain = sb.copyResource(
            "someDir/someFile.txt",
            "anotherPlace/anotherFile.txt"
        );
        // Create some file
        File f = sb.createResource(
            "wherever/whatever",
            "some content",
            Charset.forName("UTF-8")
        );
        // Do your tests
    });
}
```