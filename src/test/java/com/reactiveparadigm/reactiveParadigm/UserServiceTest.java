package com.reactiveparadigm.reactiveParadigm;

import com.reactiveparadigm.reactiveParadigm.domain.User;
import com.reactiveparadigm.reactiveParadigm.dto.OrderDto;
import com.reactiveparadigm.reactiveParadigm.dto.OrderInfoDto;
import com.reactiveparadigm.reactiveParadigm.dto.ProductDto;
import com.reactiveparadigm.reactiveParadigm.repository.UserRepository;
import com.reactiveparadigm.reactiveParadigm.service.OrderService;
import com.reactiveparadigm.reactiveParadigm.service.ProductService;
import com.reactiveparadigm.reactiveParadigm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;
    @InjectMocks
    private static UserService userService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUserOrders_ReturnsNonEmptyFlux() {
        //given
        var userId = "user1";
        var phoneNumber = "123456789";
        var productCode = "4321";
        var user = Mono.just(new User(userId, "Carlos", phoneNumber));
        var orders = Flux.fromIterable(List.of(
                new OrderDto(phoneNumber, "1234", productCode)
        ));
        var products = Mono.just(
                new ProductDto("111", productCode, "Milk", 1234f)
        );

        Mockito.when(userRepository.findById(userId)).thenReturn(user);
        Mockito.when(orderService.getUserOrders(phoneNumber)).thenReturn(orders);
        Mockito.when(productService.getProductWithHighestScore(productCode)).thenReturn(products);

        //when
        Flux<OrderInfoDto> orderInfoFlux = userService.getUserOrdersInfo(userId);

        //then
        StepVerifier.create(orderInfoFlux)
                .assertNext(userOrderInfo -> {
                    assertEquals("Carlos", userOrderInfo.getUserName());
                    assertEquals(productCode, userOrderInfo.getProductCode());
                    assertEquals("Milk", userOrderInfo.getProductName());
                })
                .verifyComplete();
    }

    @Test
    public void getUserOrders_ReturnsEmptyFlux_WhenUserIsNotFound() {
        //given
        var userId = "user1";
        Mockito.when(userRepository.findById(userId)).thenReturn(Mono.empty());

        //when
        Flux<OrderInfoDto> orderInfoFlux = userService.getUserOrdersInfo(userId);

        //then
        StepVerifier.create(orderInfoFlux)
                .expectNextCount(0)
                .verifyComplete();
    }
}
