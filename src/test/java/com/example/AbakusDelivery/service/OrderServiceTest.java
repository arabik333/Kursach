package com.example.AbakusDelivery.service;

import com.example.AbakusDelivery.dto.request.CreateOrderRequest;
import com.example.AbakusDelivery.dto.request.OrderItemRequest;
import com.example.AbakusDelivery.dto.response.OrderResponse;
import com.example.AbakusDelivery.entity.MenuItem;
import com.example.AbakusDelivery.entity.Order;
import com.example.AbakusDelivery.entity.OrderItem;
import com.example.AbakusDelivery.entity.OrderStatus;
import com.example.AbakusDelivery.entity.User;
import com.example.AbakusDelivery.repository.MenuItemRepository;
import com.example.AbakusDelivery.repository.OrderRepository;
import com.example.AbakusDelivery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    @InjectMocks
    private OrderService orderService;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);
        currentUser.setEmail("test@example.com");

        // Чистим SecurityContext перед каждым тестом
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_whenItemsEmpty_shouldThrowException() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setItems(new ArrayList<>());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.create(request)
        );

        assertTrue(ex.getMessage().toLowerCase().contains("at least one item"));
    }

    @Test
    void create_whenValidItems_shouldCalculateTotalCorrectlyAndBindToUser() {
        // given
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setMenuItemId(10L);
        item1.setQuantity(2);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setMenuItemId(20L);
        item2.setQuantity(1);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setComment("no onions");
        request.setItems(List.of(item1, item2));

        MenuItem menuItem1 = new MenuItem();
        menuItem1.setId(10L);
        menuItem1.setPrice(new BigDecimal("10.00"));

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setId(20L);
        menuItem2.setPrice(new BigDecimal("5.50"));

        // Поднимаем SecurityContext с email пользователя
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        currentUser.getEmail(), // principal / username
                        null,
                        List.of()               // роли не важны для этого теста
                );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // getCurrentUser() внутри OrderService найдёт юзера по email
        given(userRepository.findByEmail(currentUser.getEmail()))
                .willReturn(Optional.of(currentUser));

        given(menuItemRepository.findById(10L)).willReturn(Optional.of(menuItem1));
        given(menuItemRepository.findById(20L)).willReturn(Optional.of(menuItem2));

        given(orderRepository.save(any(Order.class)))
                .willAnswer(invocation -> {
                    Order o = invocation.getArgument(0);
                    o.setId(100L);
                    if (o.getItems() != null) {
                        long id = 1L;
                        for (OrderItem oi : o.getItems()) {
                            oi.setId(id++);
                        }
                    }
                    return o;
                });

        // when
        OrderResponse response = orderService.create(request);

        // then
        assertNotNull(response.getId());
        assertEquals(new BigDecimal("25.50"), response.getTotalPrice()); // 2*10 + 1*5.5
        assertEquals(OrderStatus.NEW, response.getStatus());
        assertEquals(currentUser.getId(), response.getUserId());
        assertEquals(2, response.getItems().size());
        assertEquals("no onions", response.getComment());

        verify(orderRepository).save(any(Order.class));
        verify(menuItemRepository, times(2)).findById(anyLong());
        verify(userRepository).findByEmail(currentUser.getEmail());
    }
}
