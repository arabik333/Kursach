package com.example.AbakusDelivery.service;

import com.example.AbakusDelivery.entity.OrderItem;
import com.example.AbakusDelivery.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderItemService {

    public final OrderItemRepository orderItemRepository;

    public List<OrderItem> getAllByIds;
}
