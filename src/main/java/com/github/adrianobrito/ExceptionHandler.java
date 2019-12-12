package com.github.adrianobrito;

/**
 * Created by adriano on 13/11/19.
 */
public interface ExceptionHandler<E extends Exception> {

    void handle(E exception);

}
