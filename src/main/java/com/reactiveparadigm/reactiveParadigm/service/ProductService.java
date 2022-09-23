package com.reactiveparadigm.reactiveParadigm.service;

import com.reactiveparadigm.reactiveParadigm.dto.ProductDto;
import com.reactiveparadigm.reactiveParadigm.utils.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.Duration;

@Service
@Slf4j
public class ProductService {
    private final WebClient productInfoServiceClient = WebClient.builder()
            .baseUrl("http://localhost:8082/productInfoService")
            .build();

    public Mono<ProductDto> getProductWithHighestScore(String productCode) {
        Mono<ProductDto> productMono = productInfoServiceClient.get()
                .uri(getProductInfoServiceUri(productCode))
                .retrieve()
                .bodyToFlux(ProductDto.class)
                .timeout(Duration.ofSeconds(5), Flux.error(new RuntimeException("Product info service request timed out")))
                .doOnEach(Log.logOnNext(productDto -> log.info("found product {}", productDto)))
                .doOnEach(Log.logOnError(throwable -> log.error("Exception happened: ", throwable)))
                .sort((p1, p2) -> p2.getScore().compareTo(p1.getScore()))
                .next()
                .onErrorResume((ex) -> Mono.empty());

        return Mono.just(String.format("finding products for productCode '%s'", productCode))
                .doOnEach(Log.logOnNext(log::info))
                .then(productMono);
    }

    private String getProductInfoServiceUri(String productCode) {
        return UriComponentsBuilder.fromUriString("/product/names")
                .queryParam("productCode", productCode)
                .buildAndExpand()
                .toUriString();
    }
}
