package com.ecom.shared.common.config.common;


import com.ecom.shared.common.controller.AppController;
import com.ecom.shared.common.exception.EcomExceptionHandler;
import com.ecom.shared.common.validation.DtoValidator;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(value = {DtoValidator.class, EcomExceptionHandler.class, AppController.class,WebClientConfig.class,TenantFilter.class, EcomExceptionHandler.class})
@PropertySource("classpath:common.yml")
public class ImportCommonClasses {
}
