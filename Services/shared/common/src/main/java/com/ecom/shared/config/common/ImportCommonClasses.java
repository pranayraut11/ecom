package com.ecom.shared.config.common;


import com.ecom.shared.config.i18.I18Config;
import com.ecom.shared.config.i18.Translator;
import com.ecom.shared.config.rest.WebClientConfig;
import com.ecom.shared.config.security.KeycloakConfiguration;
import com.ecom.shared.config.security.SecurityConfig;
import com.ecom.shared.exception.EcomExceptionHandler;
import com.ecom.shared.validation.DtoValidator;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(value = {DtoValidator.class, SecurityConfig.class, Translator.class, KeycloakConfiguration.class, EcomExceptionHandler.class, WebClientConfig.class, I18Config.class})
@PropertySource("classpath:common.properties")
public class ImportCommonClasses {
}
