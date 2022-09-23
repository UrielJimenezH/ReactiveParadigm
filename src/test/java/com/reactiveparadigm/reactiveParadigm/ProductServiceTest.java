package com.reactiveparadigm.reactiveParadigm;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactiveparadigm.reactiveParadigm.dto.ProductDto;
import com.reactiveparadigm.reactiveParadigm.service.ProductService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public class ProductServiceTest {
    private static final String HOST = "localhost";
    private static final int PORT = 8082;
    private static final WireMockServer server = new WireMockServer(PORT);
    private static final String endpoint = "/productInfoService/product/names";
    private static final String productCodeKey = "productCode";
    private static final String contentTypeHeaderKey = "Content-Type";

    private static final ProductService productService = new ProductService();

    @BeforeAll
    public static void init() {
        server.start();
        WireMock.configureFor(HOST, PORT);
    }

    @AfterAll
    public static void closeServer() {
        if (server.isRunning())
            server.shutdown();
    }

    @Test
    public void getProductWithHighestScore_ReturnsNonEmptyMonoWithHighestScore() {
        //given
        var productCode = "3852";
        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder()
                .withStatus(HttpStatus.OK.value())
                .withHeader(contentTypeHeaderKey, MediaType.APPLICATION_JSON_VALUE)
                .withBodyFile("responses/products.json");

        WireMock.stubFor(WireMock.get(endpoint + "?" + productCodeKey + "=" + productCode)
//                .withQueryParam(productCode, equalTo("123456789"))
                .willReturn(mockResponse));

        //when
        Mono<ProductDto> productMono = productService.getProductWithHighestScore(productCode);

        //then
        StepVerifier.create(productMono)
                .expectNextMatches(product -> product.getProductName().equals("Milk"))
                .verifyComplete();
    }

    @Test
    public void getProductWithHighestScores_ReturnsEmptyMono_WhenProductCodeIsNotFound() {
        //given
        var productCode = "12345";
        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder()
                .withStatus(HttpStatus.NOT_FOUND.value());

        WireMock.stubFor(WireMock.get(endpoint + "?" + productCodeKey + "=" + productCode)
//                .withQueryParam(productCode, equalTo(productCode))
                .willReturn(mockResponse));

        //when
        Mono<ProductDto> productMono = productService.getProductWithHighestScore(productCode);

        //then
        StepVerifier.create(productMono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void getProductWithHighestScore_ReturnsEmptyMono_WhenNoProductsAreReceived() {
        //given
        var productCode = "987654321";
        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder()
                .withStatus(HttpStatus.OK.value());

        WireMock.stubFor(WireMock.get(endpoint + "?" + productCodeKey + "=" + productCode)
//                .withQueryParam(productCode, equalTo(productCode))
                .willReturn(mockResponse));

        //when
        Mono<ProductDto> productMono = productService.getProductWithHighestScore(productCode);

        //then
        StepVerifier.create(productMono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void getProductWithHighestScore_ReturnsEmptyMono_WhenRequestTimesOut() {
        //given
        var productCode = "987654321";
        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder()
                .withFixedDelay(6000);

        WireMock.stubFor(WireMock.get(endpoint + "?" + productCodeKey + "=" + productCode)
//                .withQueryParam(productCode, equalTo(productCode))
                .willReturn(mockResponse));

        //when
        Mono<ProductDto> productMono = productService.getProductWithHighestScore(productCode);

        //then
        StepVerifier.create(productMono)
                .expectNextCount(0)
                .verifyComplete();
    }
}
