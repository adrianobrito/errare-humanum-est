package com.github.adrianobrito;

/**
 * Created by adriano on 13/11/19.
 */
@FunctionalInterface
public interface ThrowableCommand<E extends Exception> {

    void execute() throws E;

}
