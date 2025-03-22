package com.hsf302.jpa.supermarket.repository;

import com.hsf302.jpa.supermarket.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Iterable<Account> findByEmail(String email);

    Optional<Account> findAccountByEmail(String email);
    Page<Account> findAllByOrderById(Pageable page);
    void deleteById(Long id);
    Page<Account> findAllByEmailContainingIgnoreCase(String email, Pageable pageable);
}