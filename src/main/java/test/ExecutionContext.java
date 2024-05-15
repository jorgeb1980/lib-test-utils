package test;

// Models the results of the execution of some test along with whatever output it generated
public record ExecutionContext(
    Object result,
    String out,
    String err
) {}
