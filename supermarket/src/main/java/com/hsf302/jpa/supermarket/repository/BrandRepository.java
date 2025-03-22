package com.hsf302.jpa.supermarket.repository;

import com.hsf302.jpa.supermarket.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    Optional<Brand> findByName(String name);
    Page<Brand> findAllByOrderById(Pageable pageable);
    long count();
    Page<Brand> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
