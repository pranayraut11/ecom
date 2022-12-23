package org.ecom.shared.config.common;


import org.ecom.shared.config.rest.WebClientConfig;
import org.ecom.shared.config.security.KeycloakConfiguration;
import org.ecom.shared.config.security.SecurityConfig;
import org.ecom.shared.exception.EcomExceptionHandler;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Import(value = { SecurityConfig.class, KeycloakConfiguration.class, EcomExceptionHandler.class, WebClientConfig.class})
@PropertySource("classpath:common.properties")
public class ImportCommonClasses {
}
