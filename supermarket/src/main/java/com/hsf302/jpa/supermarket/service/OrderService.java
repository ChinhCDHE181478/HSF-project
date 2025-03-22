package com.hsf302.jpa.supermarket.service;

import com.hsf302.jpa.supermarket.model.Order;
import com.hsf302.jpa.supermarket.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CartService cartService;
    @Autowired
    ProductService productService;

    public Order saveOrder(Order newOrder) {
        return orderRepository.save(newOrder);
    }
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
    public Iterable<Order> getOrderHistory(String email) {
        return orderRepository.findAllByAccountEmail(email);
    }
}
