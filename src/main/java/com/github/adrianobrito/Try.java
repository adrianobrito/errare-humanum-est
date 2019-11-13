package com.github.adrianobrito;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

public class Try {

    private Exception exception;

    private Try(Exception exception) {
        this.exception = exception;
    }

    public static Try execute(ThrowableCommand command) {
        try {
            command.execute();
            return TryBuilder.builder()
                    .build();
        } catch (Exception exception) {
            return TryBuilder.builder()
                    .withException(exception)
                    .build();
        }
    }

    public  <E extends Exception> void catchWith(List<StandardExceptionHandler<? extends Exception>> handlers) {
        if(isNull(exception)) {
            return;
        }

        Class<E> exceptionClass = (Class<E>) exception.getClass();
        E castedException = exceptionClass.cast(exception);

        handlers.stream()
                .filter(exceptionHandler -> isForClass(exceptionHandler.getExceptionClass()))
                .map(handler -> (StandardExceptionHandler<E>) handler)
                .findFirst()
                .orElseThrow(() -> new UnhandledExceptionError(exception))
                .handle(castedException);
    }

    public void catchWith(StandardExceptionHandler<? extends Exception>... exceptionHandlers) {
        this.catchWith(asList(exceptionHandlers));
    }

    public void catchWith(LambdaExceptionHandler lambdaExceptionHandler) {
        lambdaExceptionHandler.handle(exception);
    }

    private boolean isForClass(Class clazz) {
        return clazz.equals(exception.getClass());
    }

    public static class TryBuilder {

        TryBuilder() {}

        private Exception exception;

        static TryBuilder builder() {
            return new TryBuilder();
        }

        TryBuilder withException(Exception exception) {
            this.exception = exception;
            return this;
        }

        Try build() {
            return new Try(this.exception);
        }

    }

}

