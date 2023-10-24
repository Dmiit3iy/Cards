package com.dmiit3iy.filter;


import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Cookie[] cookies = request.getCookies();
        System.out.println(Arrays.toString(cookies));
        String value = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("hash")) {
                    value = cookie.getValue();
                }
            }
        }

        System.out.println(request.getRequestURI());

        //URL Запроса/переадресации на Servlet входа


        //String loginURI = request.getContextPath()+"/users";
        String loginURI = request.getContextPath()+"/login";
        System.out.println("корень"+loginURI);
        String registerURI = request.getContextPath()+"/users";
        String cardsrURI = request.getContextPath()+"/cards";


        //Если сессия ранее создана
        System.out.println(request.getRequestURI());
        boolean loginRequest = request.getRequestURI().contains(loginURI);
        System.out.println(loginRequest);
        boolean registerRequest = request.getRequestURI().contains(registerURI);
        boolean cardsRequest = request.getRequestURI().contains(cardsrURI);
        //Если запрос пришел со страницы с входом или сессия не пуста даем добро следовать дальше
        //Если нет ридерект на страницу входа
        if ((request.getRequestURI().endsWith("js") || loginRequest || registerRequest||cardsRequest)
        || value != null && DAO.getObjectByParam("hash", value, User.class) != null) {
            filterChain.doFilter(request, response);
        } else {

            response.sendRedirect(loginURI+".html");
        }
    }

    @Override
    public void destroy() {
    }
}
