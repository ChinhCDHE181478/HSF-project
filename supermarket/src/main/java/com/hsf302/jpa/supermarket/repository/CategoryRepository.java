package com.hsf302.jpa.supermarket.repository;

import com.hsf302.jpa.supermarket.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    Page<Category> findAllByOrderById(Pageable pageable);
    Page<Category> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

}
