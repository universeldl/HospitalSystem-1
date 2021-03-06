package com.example.demo.config;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by next on 2018/12/19.
 */
@Component
public class axios implements Filter{
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response1 = (HttpServletResponse) response;
        HttpServletRequest request1 = (HttpServletRequest) request;
        response1.setHeader("Access-Control-Allow-Origin", "*");
        response1.setHeader("Access-Control-Allow-Credentials", "true");
        response1.setHeader("Access-Control-Allow-Methods", "*");
        response1.setHeader("Access-Control-Allow-Headers", request1.getHeader("Access-Control-Request-Headers"));
        response1.setHeader("Access-Control-Expose-Headers", "*");

        if (request1.getMethod().equals( RequestMethod.OPTIONS.toString())){
            System.out.println("-----检查------");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
