package com.github.adrianobrito;

import net.jodah.typetools.TypeResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by adriano on 13/11/19.
 */
public interface ExceptionHandler<E extends Exception> {

    void handle(E exception);

}
