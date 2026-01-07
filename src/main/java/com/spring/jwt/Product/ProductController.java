package com.spring.jwt.Product;


import com.spring.jwt.EmployeeFarmerSurvey.BaseResponseDTO1;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;


    @PostMapping("/add")
    public ResponseEntity<BaseResponseDTO1<ProductDTO>> create(
            @RequestBody ProductDTO dto) throws BadRequestException {

        ProductDTO result = productService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponseDTO1<>(
                        String.valueOf(HttpStatus.CREATED.value()),
                        "Product created successfully",
                        result
                ));
    }

    /* ================= READ ================= */

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO1<ProductDTO>> getById(
            @PathVariable Long id) throws BadRequestException {

        ProductDTO result = productService.getById(id);

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        String.valueOf(HttpStatus.OK.value()),
                        "Product fetched successfully",
                        result
                )
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponseDTO1<List<ProductDTO>>> getAll() {

        List<ProductDTO> result = productService.getAll();

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        String.valueOf(HttpStatus.OK.value()),
                        "Products fetched successfully",
                        result
                )
        );
    }



    @PatchMapping("/{id}")
    public ResponseEntity<BaseResponseDTO1<ProductDTO>> patch(
            @PathVariable Long id,
            @RequestBody ProductDTO dto) throws BadRequestException {

        ProductDTO result = productService.patch(id, dto);

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        String.valueOf(HttpStatus.OK.value()),
                        "Product updated partially",
                        result
                )
        );
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseDTO1<Void>> delete(
            @PathVariable Long id) throws BadRequestException {

        productService.delete(id);

        return ResponseEntity.ok(
                new BaseResponseDTO1<>(
                        String.valueOf(HttpStatus.OK.value()),
                        "Product deleted successfully",
                        null
                )
        );
    }
}
