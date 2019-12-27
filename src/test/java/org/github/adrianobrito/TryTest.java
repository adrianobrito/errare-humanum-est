package org.github.adrianobrito;

import org.github.adrianobrito.handlers.LambdaExceptionHandler;
import org.github.adrianobrito.handlers.StandardExceptionHandler;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class TryTest {

    @Test
    public void shouldExecuteExceptionalBlock() {
        Try.execute(this::blockWithoutException);
    }

    @Test
    public void shouldExecuteExceptionalBlockThatThrowsException() {
        LambdaExceptionHandler exceptionHandler = Assert::assertNotNull;

        Try.execute(this::blockWithException).catchWith(Assert::assertNotNull);
    }

    @Test
    public void shouldExecuteExceptionalBlockWithoutThrowException() {
        Try.execute(this::blockWithoutException).catchWith(Assert::assertNull);
    }

    @Test
    public void shouldExecuteExceptionalBlockAndCatchSpecificExceptionOnly() {
        Try.execute(this::anotherExceptionalBlock).catchWith(
                new ExampleExceptionHandler(),
                new AnotherExceptionHandler()
        );
    }

    @Test
    public void shouldExecuteExceptionalAndHandleItInLambdaBlock() {
        Try.execute(this::blockWithException).catchWith(exception -> assertNotNull(exception));
    }

    private class ExampleException extends Exception {}
    private class ExampleExceptionHandler implements StandardExceptionHandler<ExampleException> {
        @Override
        public void handle(ExampleException exception) {
            assertNotNull(exception);
        }
    }
    private class AnotherExampleException extends ExampleException {}
    private class AnotherExceptionHandler implements StandardExceptionHandler<AnotherExampleException> {
        @Override
        public void handle(AnotherExampleException exception) {
            assertNotNull(exception);
        }
    }

    private void blockWithException() throws ExampleException {
        exceptionalBlock(true);
    }

    private void blockWithoutException() throws ExampleException {
        exceptionalBlock(false);
    }

    private void exceptionalBlock(boolean shouldThrowException) throws ExampleException {
        if(shouldThrowException) {
            throw new ExampleException();
        }
    }

    public void anotherExceptionalBlock() throws AnotherExampleException {
        throw new AnotherExampleException();
    }

}
