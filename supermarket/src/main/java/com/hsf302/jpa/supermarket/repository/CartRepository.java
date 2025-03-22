package com.hsf302.jpa.supermarket.repository;

import com.hsf302.jpa.supermarket.model.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
}
