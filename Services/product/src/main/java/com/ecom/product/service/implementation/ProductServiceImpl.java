package com.ecom.product.service.implementation;

import com.ecom.product.dto.ProductDTO;
import com.ecom.product.entity.Product;
import com.ecom.product.mappers.specification.ProductMapper;
import com.ecom.product.repository.specification.ProductRepository;
import com.ecom.product.rest.FileManagerService;
import com.ecom.product.service.specification.ProductService;
import org.ecom.shared.exception.EcomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

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
        return productMapper.productToProductDTO(productRepository.findById(id).get());
    }

    @Override
    public void delete(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public void create(@Valid ProductDTO dto) {
        Product product = productRepository.save(productMapper.productDTOToProduct(dto));
        try {
            List<String> images = fileManagerService.uploadFiles(dto.getImages(), product.getId());
            product.setImages(images);
            productRepository.save(product);
        } catch (EcomException e) {
            productRepository.delete(product);
            throw new EcomException(e.getStatusCode(),e.getErrorCode(),e.getMessage(),e.isConvert());
        } catch (IOException e){
            throw new EcomException(e);
        }
    }

    @Override
    public void update(ProductDTO entity) {
        productRepository.save(productMapper.productDTOToProduct(entity));
    }
}
