package com.ecom.shared.config.security;

import com.ecom.shared.dto.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.common.VerificationException;
import org.keycloak.util.TokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

       HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
       String authToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
       if(StringUtils.hasLength(authToken)){
           try {
               UserDetails.setUserInfo(authToken.replaceFirst(TokenUtil.TOKEN_TYPE_BEARER,"").trim());
           } catch (VerificationException e) {
               throw new RuntimeException(e);
           }
       }
       filterChain.doFilter(servletRequest,servletResponse);
    }
}
