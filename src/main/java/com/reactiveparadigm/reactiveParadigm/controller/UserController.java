package com.reactiveparadigm.reactiveParadigm.controller;

import com.reactiveparadigm.reactiveParadigm.dto.OrderInfoDto;
import com.reactiveparadigm.reactiveParadigm.dto.UserDto;
import com.reactiveparadigm.reactiveParadigm.service.UserService;
import com.reactiveparadigm.reactiveParadigm.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserDto> getAllUsers(
            @RequestHeader("X-REQUEST-ID") String requestId
    ) {
        return userService.getAllUsers()
	            .contextWrite(Context.of(Log.requestIdKey, requestId));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<UserDto> getUser(
            @RequestHeader("X-REQUEST-ID") String requestId,
            @PathVariable String id
    ) {
        return userService.getUser(id)
                .contextWrite(Context.of(Log.requestIdKey, requestId));
    }

    @GetMapping(value = "/{id}/orders", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrderInfoDto> getUserOrdersInfo(
            @RequestHeader("X-REQUEST-ID") String requestId,
            @PathVariable String id
    ) {
        return userService.getUserOrdersInfo(id)
                .contextWrite(Context.of(Log.requestIdKey, requestId));
    }
}
