package com.hsf302.jpa.supermarket.service;

import com.hsf302.jpa.supermarket.repository.SelectedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectedItemService {
    @Autowired
    SelectedItemRepository selectedItemRepository;

    public void deleteById(Long id) {
        selectedItemRepository.deleteById(id);
    }
}
