package com.spring.jwt.Product;

import org.apache.coyote.BadRequestException;

import java.util.List;

public interface ProductService {

    ProductDTO create(ProductDTO dto) throws BadRequestException;

    ProductDTO getById(Long id) throws BadRequestException;

    List<ProductDTO> getAll();

    ProductDTO patch(Long id, ProductDTO dto) throws BadRequestException;

    void delete(Long id) throws BadRequestException;

}
