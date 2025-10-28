package com.ecom.shared.common.annotation.aspects;

import com.ecom.shared.common.annotation.AppendTenantId;
import com.ecom.shared.common.config.common.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AppendTenantIdAspect {

    @Before("@annotation(appendTenantId)")
    public void appendTenantId(AppendTenantId appendTenantId) {
       String tenantId =  UserContext.getUserId();
        log.info("Appending tenant ID for method: " + tenantId);
    }
}
