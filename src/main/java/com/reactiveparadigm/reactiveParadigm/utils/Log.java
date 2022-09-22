package com.reactiveparadigm.reactiveParadigm.utils;

import org.slf4j.MDC;
import reactor.core.publisher.Signal;
import java.util.Optional;
import java.util.function.Consumer;

public class Log {
    public static final String requestIdKey = "requestId";
    public static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
        return signal -> {
            if (!signal.isOnNext())
                return;

            Optional<String> requestIdMaybe = signal.getContextView().getOrEmpty(requestIdKey);

            requestIdMaybe.ifPresentOrElse(requestId -> {
                try (MDC.MDCCloseable closeable = MDC.putCloseable(requestIdKey, requestId)) {
                    logStatement.accept(signal.get());
                }
            }, () -> logStatement.accept(signal.get()));
        };
    }

    public static Consumer<Signal<?>> logOnError(Consumer<Throwable> errorLogStatement) {
        return signal -> {
            if (!signal.isOnError())
                return;

            Optional<String> requestIdMaybe = signal.getContextView().getOrEmpty(requestIdKey);

            requestIdMaybe.ifPresentOrElse(requestId -> {
                try (MDC.MDCCloseable closeable = MDC.putCloseable(requestIdKey, requestId)) {
                    errorLogStatement.accept(signal.getThrowable());
                }
            }, () -> errorLogStatement.accept(signal.getThrowable()));
        };
    }
}
