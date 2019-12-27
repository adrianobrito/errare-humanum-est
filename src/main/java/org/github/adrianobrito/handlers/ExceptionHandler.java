package org.github.adrianobrito.handlers;

/**
 * Created by adriano on 13/11/19.
 */
interface ExceptionHandler<E extends Exception> {

    void handle(E exception);

}
