package com.ecom.shared.common.config.common;


import com.ecom.shared.common.config.i18.I18Config;
import com.ecom.shared.common.config.i18.Translator;
import com.ecom.shared.common.config.rest.WebClientConfig;
import com.ecom.shared.common.config.security.SecurityConfig;
import com.ecom.shared.common.controller.AppController;
import com.ecom.shared.common.exception.EcomExceptionHandler;
import com.ecom.shared.common.validation.DtoValidator;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(value = {DtoValidator.class, SecurityConfig.class, Translator.class, EcomExceptionHandler.class, WebClientConfig.class, I18Config.class, AppController.class})
@PropertySource("classpath:common.properties")
public class ImportCommonClasses {
}
