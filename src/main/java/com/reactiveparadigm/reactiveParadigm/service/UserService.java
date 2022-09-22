package com.reactiveparadigm.reactiveParadigm.service;

import com.reactiveparadigm.reactiveParadigm.converter.UserConverter;
import com.reactiveparadigm.reactiveParadigm.dto.*;
import com.reactiveparadigm.reactiveParadigm.repository.UserRepository;
import com.reactiveparadigm.reactiveParadigm.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;

    public Flux<UserDto> getAllUsers() {
        Flux<UserDto> userFlux = userRepository.findAll()
                .map(UserConverter::entityToDto);

        return Mono.just("finding all users for requestId")
                .doOnEach(Log.logOnNext(log::info))
                .thenMany(userFlux)
                .doOnEach(Log.logOnNext(userOrderInfo -> log.info("found user {}", userOrderInfo)));
    }

    public Mono<UserDto> getUser(String id) {
        Mono<UserDto> userMono = userRepository.findById(id)
                .map(UserConverter::entityToDto);

        return Mono.just(String.format("finding user for id '%s'", id))
                .doOnEach(Log.logOnNext(log::info))
                .then(userMono)
                .doOnEach(Log.logOnNext(user -> log.info("found user {}", user)));
    }

    public Flux<OrderInfoDto> getUserOrdersInfo(String id) {
        return userRepository.findById(id)
                .flatMapMany(u -> {
                    UserDto user = UserConverter.entityToDto(u);

                    Flux<OrderInfoDto> orderInfoDtoFlux = orderService.getUserOrders(user.getPhone())
                            .flatMap(order -> {
                                Mono<ProductDto> product = productService.getProductWithHighestScore(order.getProductCode());

                                return Mono.just(order)
                                        .zipWith(product, OrderInfoDto::new);
                            })
                            .map(orderInfoDto -> {
                                orderInfoDto.setUserName(user.getName());
                                return orderInfoDto;
                            });

                    return Mono.just(String.format("finding user for id '%s'", id))
                            .doOnEach(Log.logOnNext(log::info))
                            .thenMany(orderInfoDtoFlux)
                            .doOnEach(Log.logOnNext(userOrderInfo -> log.info("found user order info {}", userOrderInfo)));
                });
    }
}
