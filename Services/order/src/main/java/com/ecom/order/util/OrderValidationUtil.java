package com.ecom.order.util;

import com.ecom.order.dto.CreateOrderDTO;
import com.ecom.order.dto.OrderProductInput;
import com.ecom.order.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderValidationUtil {

    public static void validateOrderRequest(CreateOrderDTO createOrderDTO) {
        // Validate address
        if (createOrderDTO.getAddressId() == null || createOrderDTO.getAddressId().trim().isEmpty()) {
            throw new IllegalArgumentException("VALIDATION_ERROR: " + MessageUtil.getMessage("validation.address.required"));
        }

        // Validate payment info
        if (createOrderDTO.getPaymentMode() == null || createOrderDTO.getPaymentMode().trim().isEmpty()) {
            throw new IllegalArgumentException("VALIDATION_ERROR: " + MessageUtil.getMessage("validation.payment.mode.required"));
        }

        if (createOrderDTO.getPaymentProvider() == null || createOrderDTO.getPaymentProvider().trim().isEmpty()) {
            throw new IllegalArgumentException("VALIDATION_ERROR: " + MessageUtil.getMessage("validation.payment.provider.required"));
        }

        // Validate products list
        if (createOrderDTO.getProducts() == null || createOrderDTO.getProducts().isEmpty()) {
            throw new IllegalArgumentException("VALIDATION_ERROR: " + MessageUtil.getMessage("validation.products.empty"));
        }
        
        // Validate coupon code if provided
        if (createOrderDTO.getCouponCode() != null && !createOrderDTO.getCouponCode().isEmpty()) {
            if ("INVALID".equals(createOrderDTO.getCouponCode())) {
                throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                    MessageUtil.getMessage("validation.coupon.invalid", createOrderDTO.getCouponCode()));
            }
            // Add more coupon validation logic here
        }
    }

    public static List<Product> validateAndMapProducts(CreateOrderDTO createOrderDTO, List<Product> foundProducts) {
        // First validate the overall order request
        validateOrderRequest(createOrderDTO);
        
        Map<String, Product> productMap = foundProducts.stream()
                .collect(Collectors.toMap(
                        p -> p.getProductId() + "::" + p.getSellerId(),
                        p -> p
                ));
        List<Product> validatedProducts = new java.util.ArrayList<>();
        // Track duplicates
        java.util.Set<String> seenKeys = new java.util.HashSet<>();
        for (OrderProductInput input : createOrderDTO.getProducts()) {
            String key = input.getProductId() + "::" + input.getSellerId();
            if (!seenKeys.add(key)) {
                throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                    MessageUtil.getMessage("validation.product.duplicate", key));
            }
            Product actual = productMap.get(key);
            if (actual == null) {
                throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                    MessageUtil.getMessage("validation.product.notfound", key));
            }
            if (input.getQuantity() <= 0) {
                throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                    MessageUtil.getMessage("validation.product.quantity", key));
            }
            if (!actual.isInStock()) {
                throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                    MessageUtil.getMessage("validation.product.outofstock", key));
            }
            if (!actual.isActive()) {
                throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                    MessageUtil.getMessage("validation.product.inactive", key));
            }
            
            // Check price mismatch if price is provided in input
            if (input.getPrice() != null && 
                actual.getPrice() != null && 
                input.getPrice().compareTo(BigDecimal.ZERO) > 0 && 
                input.getPrice().subtract(actual.getPrice()).abs().compareTo(new BigDecimal("0.01")) > 0) {
                throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                    MessageUtil.getMessage("validation.product.price", key));
            }
            
            // Add more checks as needed
            actual.setQuantity((short) input.getQuantity());
            actual.setSku(input.getSku());
            validatedProducts.add(actual);
        }
        if (validatedProducts.isEmpty()) {
            throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                MessageUtil.getMessage("validation.products.empty"));
        }
        
        // Cross-seller validation - all products must be from same seller
        String firstSellerId = validatedProducts.get(0).getSellerId();
        boolean hasDifferentSeller = validatedProducts.stream()
            .anyMatch(p -> !p.getSellerId().equals(firstSellerId));
        if (hasDifferentSeller) {
            throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                MessageUtil.getMessage("validation.order.crossseller"));
        }
        
        // Example: order size limit
        if (validatedProducts.size() > 100) {
            throw new IllegalArgumentException("VALIDATION_ERROR: " + 
                MessageUtil.getMessage("validation.order.sizelimit"));
        }
        
        return validatedProducts;
    }
}
