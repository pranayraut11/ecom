package com.ecom.shared.common.config.security;

import com.ecom.shared.common.dto.UserDetails;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class HttpRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

       HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
       String authToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
       if(StringUtils.hasLength(authToken)){
           try {
               UserDetails.setUserInfo(authToken.replaceFirst(OAuth2AccessToken.TokenType.BEARER.getValue(),"").trim());
           } catch (Exception e) {
               throw new RuntimeException(e);
           }
       }
       filterChain.doFilter(servletRequest,servletResponse);
    }
}
