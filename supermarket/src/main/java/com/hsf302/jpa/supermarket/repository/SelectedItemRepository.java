package com.hsf302.jpa.supermarket.repository;

import com.hsf302.jpa.supermarket.model.Product;
import com.hsf302.jpa.supermarket.model.SelectedItem;
import com.hsf302.jpa.supermarket.model.SelectedList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SelectedItemRepository extends JpaRepository<SelectedItem, Long> {
    void deleteBySelectedListAndProduct(SelectedList list, Product product);
}