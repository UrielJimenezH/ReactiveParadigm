package com.reactiveparadigm.reactiveParadigm.service;

import com.reactiveparadigm.reactiveParadigm.dto.OrderDto;
import com.reactiveparadigm.reactiveParadigm.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

@Service
@Slf4j
public class OrderService {
    private final WebClient orderSearchServiceClient = WebClient.builder()
            .baseUrl("http://localhost:8081/orderSearchService")
            .build();

    public Flux<OrderDto> getUserOrders(String phoneNumber, String requestId) {
        return orderSearchServiceClient.get()
                .uri(getOrderSearchServiceUri(phoneNumber))
                .retrieve()
                .bodyToFlux(OrderDto.class)
                .doOnEach(Log.logOnNext(orderDto -> log.info("found order {}", orderDto)))
                .contextWrite(Context.of(Log.requestIdKey, requestId));
    }

    private String getOrderSearchServiceUri(String phoneNumber) {
        return UriComponentsBuilder.fromUriString("/order/phone")
                .queryParam("phoneNumber", phoneNumber)
                .buildAndExpand()
                .toUriString();
    }
}
