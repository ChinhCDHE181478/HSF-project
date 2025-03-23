package com.hsf302.jpa.supermarket.repository;

import com.hsf302.jpa.supermarket.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Iterable<Order> findAllByAccountEmail(String email);

    Page<Order> findByStatus(String status, Pageable pageable);
}
