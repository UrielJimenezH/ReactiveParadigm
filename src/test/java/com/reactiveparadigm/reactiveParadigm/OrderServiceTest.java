package com.reactiveparadigm.reactiveParadigm;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactiveparadigm.reactiveParadigm.dto.OrderDto;
import com.reactiveparadigm.reactiveParadigm.service.OrderService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class OrderServiceTest {
    private static final String HOST = "localhost";
    private static final int PORT = 8081;
    private static final WireMockServer server = new WireMockServer(PORT);
    private static final String endpoint = "/orderSearchService/order/phone";
    private static final String phoneNumberParamKey = "phoneNumber";
    private static final String contentTypeHeaderKey = "Content-Type";

    private static final OrderService orderService = new OrderService();

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
    public void getUserOrders_ReturnsNonEmptyFlux() {
        //given
        var phoneNumber = "123456789";
        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder()
                .withStatus(HttpStatus.OK.value())
                .withHeader(contentTypeHeaderKey, MediaType.APPLICATION_NDJSON_VALUE)
                .withBodyFile("responses/orders.ndjson");
//                .withLogNormalRandomDelay(9000, 0.1);

        WireMock.stubFor(WireMock.get(endpoint + "?" + phoneNumberParamKey + "=" + phoneNumber)
//                .withQueryParam(phoneNumberParamKey, equalTo("123456789"))
                .willReturn(mockResponse));

        //when
        Flux<OrderDto> orderFlux = orderService.getUserOrders(phoneNumber);

        //then
        StepVerifier.create(orderFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getUserOrders_ReturnsEmptyFlux_WhenPhoneNumberIsNotFound() {
        //given
        var phoneNumber = "12345";
        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder()
                .withStatus(HttpStatus.NOT_FOUND.value());

        WireMock.stubFor(WireMock.get(endpoint + "?" + phoneNumberParamKey + "=" + phoneNumber)
//                .withQueryParam(phoneNumberParamKey, equalTo("123456789"))
                .willReturn(mockResponse));

        //when
        Flux<OrderDto> orderFlux = orderService.getUserOrders(phoneNumber);

        //then
        StepVerifier.create(orderFlux)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void getUserOrders_ReturnsEmptyFlux_WhenNoOrdersAreReceived() {
        //given
        var phoneNumber = "987654321";
        ResponseDefinitionBuilder mockResponse = new ResponseDefinitionBuilder()
                .withStatus(HttpStatus.OK.value());

        WireMock.stubFor(WireMock.get(endpoint + "?" + phoneNumberParamKey + "=" + phoneNumber)
//                .withQueryParam(phoneNumberParamKey, equalTo("123456789"))
                .willReturn(mockResponse));

        //when
        Flux<OrderDto> orderFlux = orderService.getUserOrders(phoneNumber);

        //then
        StepVerifier.create(orderFlux)
                .expectNextCount(0)
                .verifyComplete();
    }
}
