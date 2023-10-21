package com.dmiit3iy.servlets;

import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.ResponseResult;
import com.dmiit3iy.model.User;
import com.dmiit3iy.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class AuthorizationServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setCharacterEncoding("UTF-8");

            req.setCharacterEncoding("utf-8");

            String login = req.getParameter("login");
            String password = req.getParameter("password");

            if (login != null && password != null) {
                try {
                    User user = (User) DAO.getObjectByParams(
                            new String[]{"login", "password"},
                            new Object[]{login, password}, User.class);
                    DAO.closeOpenedSession();
                    if (user != null) {
                        String hash = StringUtil.generateHash();
                        user.setHash(hash);
                        DAO.updateObject(user);
                        Cookie cookie = new Cookie("hash", hash);
                        cookie.setMaxAge(30 * 60);
                        cookie.setPath("/");
                        resp.addCookie(cookie);
                        Cookie cookieUserId = new Cookie("user", String.valueOf(user.getId()));
                        cookieUserId.setMaxAge(30 * 60);
                        cookieUserId.setPath("/");
                        resp.addCookie(cookieUserId);
                    } else {
                        resp.setStatus(400);
                        resp.getWriter().print("incorrect login or password");
                    }

                } catch (Exception e) {
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
                }
            }
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            req.setCharacterEncoding("utf-8");
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            String id = req.getParameter("id");
            if (id != null) {
                try {
                    User user = (User) DAO.getObjectById(Long.parseLong(id), User.class);
                    DAO.closeOpenedSession();
                    if (user != null) {
                        user.setHash(null);
                        Cookie[] cookies = req.getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                cookie.setValue(null);
                                cookie.setMaxAge(0);
                                cookie.setPath("/");
                                resp.addCookie(cookie);
                            }
                        }else {
                            resp.setStatus(400);
                            resp.getWriter().write("No cookie");
                        }
                        DAO.updateObject(user);
                        this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
                    }else {
                        resp.setStatus(400);
                        resp.getWriter().write("No such user");
                    }
                } catch (NumberFormatException e) {
                    resp.setStatus(400);
                    resp.getWriter().println(e.getMessage());
                }
            }else {
                resp.setStatus(400);
                resp.getWriter().write("Incorrect id");
            }
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            req.setCharacterEncoding("utf-8");
            try {
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, DAO.getAllObjects(User.class)));
            } catch (Exception e) {
                resp.setStatus(200);
            }
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setCharacterEncoding("UTF-8");

            req.setCharacterEncoding("utf-8");

            String id = req.getParameter("id");
            if (id != null) {
                try {
                    DAO.deleteObjectById(Long.valueOf(id), User.class);
                } catch (Exception e) {
                    resp.setStatus(200);
                }
            }
        }
}
