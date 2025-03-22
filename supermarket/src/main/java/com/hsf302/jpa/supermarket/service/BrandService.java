package com.hsf302.jpa.supermarket.service;

import com.hsf302.jpa.supermarket.model.Brand;
import com.hsf302.jpa.supermarket.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BrandService {
    @Autowired
    BrandRepository brandRepository;
    public Page<Brand> searchAdmin(String name, int page){
        return brandRepository.findAllByNameContainingIgnoreCase(name,PageRequest.of(page, 15));
    }
    public Brand getBrandByName(String name) {
        return brandRepository.findByName(name).orElse(null);
    }
    public void delete(Integer id){
        brandRepository.deleteById(id);
    }
    public Brand getById(Integer id){
        return brandRepository.findById(id).orElse(null);
    }
    public long count(){
        return brandRepository.count();
    }
    public Brand addBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    public Iterable<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
    public Page<Brand> getAllByPage(int page){
        Pageable pageable = PageRequest.of(page, 15);
        return brandRepository.findAllByOrderById(pageable);
    }
}
