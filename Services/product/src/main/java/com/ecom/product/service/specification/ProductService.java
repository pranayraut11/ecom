package com.ecom.product.service.specification;

import com.ecom.product.dto.PriceDTO;
import com.ecom.product.dto.ProductDTO;
import com.ecom.shared.common.dto.PageRequestDTO;
import com.ecom.shared.common.dto.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    List<ProductDTO> getAll();

    PageResponse getAll(PageRequestDTO pageRequestDTO);

    ProductDTO get(String id);

    void delete(List<String> id, String productId) throws IOException;

    void create(ProductDTO productDTO, List<MultipartFile> files);

    void update(ProductDTO entity);

    void addSellerToProduct(PriceDTO priceDTO);
}
