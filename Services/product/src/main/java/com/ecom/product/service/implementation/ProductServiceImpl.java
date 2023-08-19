package com.ecom.product.service.implementation;

import com.ecom.product.constant.Collections;
import com.ecom.product.constant.LamdaExpressions;
import com.ecom.product.constant.MessageConstants;
import com.ecom.product.dto.PriceDTO;
import com.ecom.product.dto.ProductDTO;
import com.ecom.product.entity.Product;
import com.ecom.product.mappers.specification.PriceMapper;
import com.ecom.product.mappers.specification.ProductMapper;
import com.ecom.product.repository.specification.ProductRepository;
import com.ecom.product.rest.FileManagerService;
import com.ecom.product.service.specification.ProductService;
import com.ecom.shared.common.exception.EcomException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PriceMapper priceMapper;

    @Autowired
    private FileManagerService fileManagerService;


    @Override
    public List<ProductDTO> getAll() {
        List<ProductDTO> productList = new ArrayList<>();
        productRepository.findAll().forEach(product -> productList.add(productMapper.productToProductDTO(product)));
        return productList;
    }

    @Override
    public ProductDTO get(String id) {
        return productMapper.productToProductDTO(productRepository.findById(id).orElseThrow(LamdaExpressions.throwNotFoundException(MessageConstants.ENTITY_NOT_FOUND, Collections.PRODUCT, id)));
    }

    @Override
    public void delete(List<String> ids, String productId) throws IOException {
        fileManagerService.deleteFiles(ids, productId);
        productRepository.deleteAllById(ids);
    }

    @Override
    public void create(@Valid ProductDTO dto, List<MultipartFile> files) {
        log.info("Service : Creating new product {} ", dto.getName());
        Product product = productRepository.save(productMapper.productDTOToProduct(dto));
        try {
            List<String> images = fileManagerService.uploadFiles(files, product.getId());
            product.setImages(images);
            productRepository.save(product);
        } catch (EcomException e) {
            productRepository.delete(product);
            throw new EcomException(e.getStatusCode(), e.getErrorCode(), e.getMessage());
        } catch (IOException e) {
            throw new EcomException(e);
        }

        log.info("Service :New product created successfully {} Id {} ", product.getName(), product.getId());
    }

    @Override
    public void update(ProductDTO entity) {
        productRepository.save(productMapper.productDTOToProduct(entity));
    }

    @Override
    public void addSellerToProduct(@NotNull PriceDTO priceDTO) {
        Product product = productRepository.findById(priceDTO.getProductId()).orElseThrow(LamdaExpressions.throwNotFoundException(MessageConstants.ENTITY_NOT_FOUND, Collections.PRODUCT, priceDTO.getProductId()));
        product.getPrices().add(priceMapper.priceDtoToPrice(priceDTO));
        productRepository.save(product);
    }
}
