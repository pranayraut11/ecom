package org.ecom.shared.config.i18;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Translator {

    private ResourceBundleMessageSource messageSource;

    public Translator(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String translate(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }
}
