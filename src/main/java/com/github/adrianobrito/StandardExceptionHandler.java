package com.github.adrianobrito;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface StandardExceptionHandler<E extends Exception> extends ExceptionHandler<E> {

    default Class getExceptionClass() {
        Class clazz = getClass();
        ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericInterfaces()[0];
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        Class<?> typeArgument = (Class<?>) typeArguments[0];
        return typeArgument;
    }

}
