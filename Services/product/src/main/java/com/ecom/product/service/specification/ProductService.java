package com.ecom.product.service.specification;

import com.ecom.product.dto.PageRequest;
import com.ecom.product.dto.PageResponse;
import com.ecom.product.dto.PriceDTO;
import com.ecom.product.dto.ProductDTO;
import com.ecom.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<ProductDTO> getAll();

    PageResponse getAll(PageRequest pageRequest);

    ProductDTO get(String id);

    void delete(List<String> id, String productId) throws IOException;

    void create(ProductDTO productDTO, List<MultipartFile> files);

    void update(ProductDTO entity);

    void addSellerToProduct(PriceDTO priceDTO);
}
