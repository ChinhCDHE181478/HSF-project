package com.hsf302.jpa.supermarket.service;

import com.hsf302.jpa.supermarket.model.Order;
import com.hsf302.jpa.supermarket.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    public Order saveOrder(Order newOrder) {
        return orderRepository.save(newOrder);
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Iterable<Order> getOrderHistory(String email) {
        return orderRepository.findAllByAccountEmail(email);
    }

    public Page<Order> getAllOrders(int page) {
        return orderRepository.findAll(PageRequest.of(page, 10));
    }

    public Page<Order> getOrdersByStatus(String status, int page) {
        return orderRepository.findByStatus(status, PageRequest.of(page, 10));
    }
}