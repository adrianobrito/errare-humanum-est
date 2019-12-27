# Errare Humanum Est 
![](https://github.com/adrianobrito/errare-humanum-est/workflows/Pull%20Request/badge.svg)

Exceptions can be handled in an alternative fashion:

```java
List<StandardExceptionHandler> exceptionHandlers = exceptionHandlerProvider.getExceptionHandlers();
Try.execute(this::codeBlock).catchWith(exceptionHandlers);
```

## Install

```xml
<dependency>
  <groupId>org.github.adrianobrito</groupId>
  <artifactId>errare-humanum-est</artifactId>
  <version>1.0.1</version>
</dependency>
```

## Motivation

When a code block can throws different exceptions, those exceptions are caught and handled in the same code block, 
like it can be verified below:

```java
try {
    this.codeBlock();
} catch(IOException ioException) {
    // ...
} catch(ClassCastException classCastException) {
    //...
} catch(MalformedURLException malformedURLException) {
    //...
}
```

A code block can become quite large if there is more than one exception to handle. This fact makes some developers 
define a generic block to handle an unique exception, like this:

```java
try {
    this.codeBlock();
} catch(Exception ioException) {
    // ...
}
```

Although every Java developer knows how much this approach is risky, they tend to keep it because it doesn't pollute a codebase as the usual approach does. Having it mind, `errare-humanum-est` introduces a different  
and modular approach to handle exceptions, that allow us to handle multiple exceptions like it should be done, 
and have a simple and small code block for it, as developers want.

## How to use

Let's suppose the existence of a method that throws multiple checked exceptions(or even unchecked) like the one 
described below:

```java
void storeFileAndPublishResult() throws IOException, MalformedURLException { ... }
``` 

For that particular method, we can define handler objects that will react to the exceptions thrown by a code block. For 
the example above, it can defined the following handlers:

```java
public class IOExceptionHandler implements StandardExceptionHandler<IOException> {
    
    private final Log logger = LoggerFactory.getLogger();

    @Override
    public void handle(IOException exception) {
        logger.error("IO error: ", exception.getMessage());
        sendErrorMetrics();
        clearIOBuffers();        
    }
    
    // ...

}

public class MalformedURLExceptionHandler implements StandardExceptionHandler<MalformedURLException> {
    
    private final Log logger = LoggerFactory.getLogger();

    @Override
    public void handle(MalformedURLException exception) {
        logger.error("Url is not well formatted");
    }
}
```

Once the handlers are defined, the exceptional method can be executed like the way described below:

```java
Try.execute(() -> storeFileAndPublishResult())
   .catchWith(new IOExceptionHandler(), new MalformedURLException());
```

It is possible to put all the handlers in a list and write a code like this:

```java
List<StandardExceptionHandler> handlers = Arrays.asList(new IOExceptionHandler(), new MalformedURLException());
Try.execute(() -> storeFileAndPublishResult()).catchWith(handlers);
```

Once the block is executed, it will try to find some handler that matches with thrown exception. If any handler matches
the exception, an `UnhandledExceptionError` is thrown.

### Integrating with Spring

For those who use Spring framework, `errare-humanum-est` comes like a glove. Let's suppose that there is a Spring-based
application where it will be executed our already defined `storeFileAndPublishResult()` method.

Firstly, it can be defined as an interface that will represent the errors thrown in that method. Let's define it
as `RemoteFileExceptionHandler`:

```java
public interface RemoteFileExceptionHandler<E extends Exception> extends StandardExceptionHandler<E> { }
```

Once we have an interface to represent the errors of `storeFileAndPublishResult()` method, it can be added some changes at 
already created handlers:

```java
@Component
public class IOExceptionHandler implements RemoteFileExceptionHandler<IOException> {
    // ...
}

@Component
public class MalformedURLExceptionHandler implements RemoteFileExceptionHandler<MalformedURLException> {
    // ...
}
```

> Please check that both handlers are defined as Spring components, and they can be injected into another spring components. 

Now, the code can be simply written like this:

```java
@Component
public class RemoteFileStorage {
    
    @Autowire
    private List<RemoteFileExceptionHandler> remoteFileHandlers;
    
    public void store() {
        Try.execute(this::storeFileAndPublishResult).catchWith(remoteFileHandlers);
    }   
    
    private void storeFileAndPublishResult() throws IOException, MalformedURLException { ... }

}
```

Now, some clarifications:

1. Spring can look up for all accessible components that implement a specific interface(`RemoteFileExceptionHandler` 
in our case) and inject them, like it was done to inject all exception handlers interfaces.
2. Since it was defined a specific interface to handle remote file errors, it can be defined other ones to another
very specific use case.
3. The approach doesn't make you need to define a big try/catch block to handle the expected errors.

## Concerns

After make an introduction to the core feature of `errare-humanum-est`, it's about time to explain some details behind 
its implementation, as listed below:

* Different than usual Java exception handling, `errare-humanum-est` doesn't validate code that throws checked exceptions
at compile time.
* The library is something that makes it easier to design a modular approach to handle errors. Due to that, you can only
specify one single anonymous Java lambda as the parameter of method `catchWith()` like can seem forward:

```java
Try.execute(this::storeFileAndPublishResult).catchWith(exception -> { ... });
``` 

* If multiple handlers are defined for the same exception in `catchWith()` method, only the first which matches 
the exception will be used to handle that exception.
* Unchecked exceptions(based in `RuntimeException`) needs to be handled as well, or an `UnhandledExceptionError` will 
be thrown if some of that is thrown.
