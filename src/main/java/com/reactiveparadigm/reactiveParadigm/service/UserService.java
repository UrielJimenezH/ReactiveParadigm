package com.reactiveparadigm.reactiveParadigm.service;

import com.reactiveparadigm.reactiveParadigm.converter.UserConverter;
import com.reactiveparadigm.reactiveParadigm.dto.*;
import com.reactiveparadigm.reactiveParadigm.repository.UserRepository;
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
        return userRepository.findAll()
                .map(UserConverter::entityToDto);
    }

    public Mono<UserDto> getUser(String id) {
        return userRepository.findById(id)
                .map(UserConverter::entityToDto);
    }

    public Flux<OrderInfoDto> getUserOrdersInfo(String id, String requestId) {
        return userRepository.findById(id)
                .flatMapMany(u -> {
                    UserDto user = UserConverter.entityToDto(u);

                    return orderService.getUserOrders(user.getPhone(), requestId)
                            .flatMap(order -> {
                                Mono<ProductDto> product = productService.getProductWithHighestScore(
                                        order.getProductCode(),
                                        requestId
                                );

                                return Mono.just(order)
                                        .zipWith(product, OrderInfoDto::new);
                            })
                            .map(orderInfoDto -> {
                                orderInfoDto.setUserName(user.getName());
                                return orderInfoDto;
                            });
                });
    }
}
