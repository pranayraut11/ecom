package org.ecom.shared.config.common;


import org.ecom.shared.config.i18.I18Config;
import org.ecom.shared.config.i18.Translator;
import org.ecom.shared.config.rest.WebClientConfig;
import org.ecom.shared.config.security.KeycloakConfiguration;
import org.ecom.shared.config.security.SecurityConfig;
import org.ecom.shared.exception.EcomExceptionHandler;
import org.ecom.shared.validation.DtoValidator;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(value = {DtoValidator.class, SecurityConfig.class, Translator.class, KeycloakConfiguration.class, EcomExceptionHandler.class, WebClientConfig.class, I18Config.class})
@PropertySource("classpath:common.properties")
public class ImportCommonClasses {
}
