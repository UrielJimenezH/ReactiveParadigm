package com.reactiveparadigm.reactiveParadigm.controller;

import com.reactiveparadigm.reactiveParadigm.dto.OrderInfoDto;
import com.reactiveparadigm.reactiveParadigm.dto.UserDto;
import com.reactiveparadigm.reactiveParadigm.service.UserService;
import com.reactiveparadigm.reactiveParadigm.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public Flux<UserDto> getAllUsers(
            @RequestHeader("X-REQUEST-ID") String reqId
    ) {
        String requestId = reqId == null ? "" : reqId;

        return Mono.just(String.format("finding all users for requestId  '%s'", requestId))
                .doOnEach(Log.logOnNext(log::info))
                .thenMany(userService.getAllUsers())
                .doOnEach(Log.logOnNext(userOrderInfo -> log.info("found user {}", userOrderInfo)))
	            .contextWrite(Context.of(Log.requestIdKey, requestId));
    }

    @GetMapping("/{id}")
    public Mono<UserDto> getUser(
            @RequestHeader("X-REQUEST-ID") String reqId,
            @PathVariable String id
    ) {
        String requestId = reqId == null ? "" : reqId;

        return Mono.just(String.format("finding user for id '%s' for requestId '%s'", id, requestId))
                .doOnEach(Log.logOnNext(log::info))
                .then(userService.getUser(id))
                .doOnEach(Log.logOnNext(user -> log.info("found user {}", user)))
                .contextWrite(Context.of(Log.requestIdKey, requestId));
    }

    @GetMapping("/{id}/orders")
    public Flux<OrderInfoDto> getUserOrdersInfo(
            @RequestHeader("X-REQUEST-ID") String reqId,
            @PathVariable String id
    ) {
        String requestId = reqId == null ? "" : reqId;

        return Mono.just(String.format("finding user for id '%s' for requestId '%s'", id, requestId))
                .doOnEach(Log.logOnNext(log::info))
                .thenMany(userService.getUserOrdersInfo(id, requestId))
                .doOnEach(Log.logOnNext(user -> log.info("found user order info {}", user)))
                .contextWrite(Context.of(Log.requestIdKey, requestId));
    }

}
