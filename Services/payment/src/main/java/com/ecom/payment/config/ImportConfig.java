package com.ecom.payment.config;

import com.ecom.shared.config.common.ImportCommonClasses;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value ={ImportCommonClasses.class})
public class ImportConfig {
}
