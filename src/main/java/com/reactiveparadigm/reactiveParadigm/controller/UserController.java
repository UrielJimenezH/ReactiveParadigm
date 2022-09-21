package com.reactiveparadigm.reactiveParadigm.controller;

import com.reactiveparadigm.reactiveParadigm.dto.UserDto;
import com.reactiveparadigm.reactiveParadigm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;
import java.util.Optional;
import java.util.function.Consumer;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    private static final String requestIdKey = "requestId";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Flux<UserDto> getAllUsers(
            @RequestHeader("X-REQUEST-ID") String reqId
    ) {
        String requestId = reqId == null ? "" : reqId;

        return Mono.just(String.format("finding all users for requestId  '%s'", requestId))
                .doOnEach(logOnNext(LOGGER::info))
                .thenMany(userService.getAllUsers())
                .doOnEach(logOnNext(user -> LOGGER.info("found user {}", user)))
	            .contextWrite(Context.of(requestIdKey, requestId));

    }

    @GetMapping("/{id}")
    public Mono<UserDto> getUser(
            @RequestHeader("X-REQUEST-ID") String reqId,
            @PathVariable String id
    ) {
        String requestId = reqId == null ? "" : reqId;

        return Mono.just(String.format("finding user for id '%s' for requestId '%s'", id, requestId))
                .doOnEach(logOnNext(LOGGER::info))
                .then(userService.getUser(id))
                .doOnEach(logOnNext(user -> LOGGER.info("found user {}", user)))
                .contextWrite(Context.of(requestIdKey, requestId));
    }

    private static <T> Consumer<Signal<T>> logOnNext(Consumer<T> logStatement) {
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
}
