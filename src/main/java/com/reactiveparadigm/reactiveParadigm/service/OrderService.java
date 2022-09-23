package com.reactiveparadigm.reactiveParadigm.service;

import com.reactiveparadigm.reactiveParadigm.dto.OrderDto;
import com.reactiveparadigm.reactiveParadigm.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OrderService {
    private final WebClient orderSearchServiceClient = WebClient.builder()
            .baseUrl("http://localhost:8081/orderSearchService")
            .build();

    public Flux<OrderDto> getUserOrders(String phoneNumber) {
        Flux<OrderDto> ordersFlux = orderSearchServiceClient.get()
                .uri(getOrderSearchServiceUri(phoneNumber))
                .retrieve()
                .bodyToFlux(OrderDto.class)
                .doOnEach(Log.logOnNext(orderDto -> log.info("found order {}", orderDto)))
                .onErrorResume((ex) -> Flux.empty());

        return Mono.just(String.format("finding orders for user with phoneNumber '%s'", phoneNumber))
                .doOnEach(Log.logOnNext(log::info))
                .thenMany(ordersFlux);
    }

    private String getOrderSearchServiceUri(String phoneNumber) {
        return UriComponentsBuilder.fromUriString("/order/phone")
                .queryParam("phoneNumber", phoneNumber)
                .build()
                .toUriString();
    }
}
