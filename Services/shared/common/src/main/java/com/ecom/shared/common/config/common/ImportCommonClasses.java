package com.ecom.shared.common.config.common;


import com.ecom.shared.common.annotation.aspects.AppendTenantIdAspect;
import com.ecom.shared.common.config.httpclient.HttpClientConfig;
import com.ecom.shared.common.controller.AppController;
import com.ecom.shared.common.exception.EcomExceptionHandler;
import com.ecom.shared.common.validation.DtoValidator;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Import(value = {
    DtoValidator.class,
    EcomExceptionHandler.class,
    AppController.class,
    TenantFilter.class,
    HttpClientConfig.class,
    AppendTenantIdAspect.class,
})
@PropertySources({@PropertySource("classpath:common.yml"),@PropertySource("classpath:application.yml")})
public class ImportCommonClasses {
}
