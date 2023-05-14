package com.ecom.orchestrator.config;

import ccom.ecom.shared.common.config.ImportCommonClasses;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value ={ImportCommonClasses.class})
public class ImportConfig {
}