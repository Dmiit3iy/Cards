package com.dmiit3iy.servlets;

import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.ResponseResult;
import com.dmiit3iy.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        if (req.getParameter("name") != null && req.getParameter("login") != null && req.getParameter("password") != null) {
            String name = req.getParameter("name");
            String login = req.getParameter("login");
            String password = req.getParameter("password");
            User user = new User(login, password, name, LocalDateTime.now());
            try {
                DAO.addObject(user);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
            } catch (IllegalArgumentException e) {
                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        if (id != null&login==null&password==null) {
            try {
                User user = (User) DAO.getObjectById(Long.valueOf(id), User.class);
                if (user == null) {
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                            ("Пользователь отсутвует в базе данных", null));
                } else {
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
                }
                DAO.closeOpenedSession();
            } catch (NumberFormatException e) {
                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
            }
        }
        if (id == null&login!=null&password!=null) {
            String[] param = {login,password};
            String[] param2 ={"login","password"};
            User user = (User) DAO.getObjectByParams(param2,param, User.class);
            if (user == null) {
                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                        ("Введены неверные данные или пользователь отсутвует в базе данных", null));

            } else {
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
            }
            DAO.closeOpenedSession();
        }
    }

    /**
     * осуществляет удаление пользователя с заданным id из базы данных,
     * а так же каскадное удаление всей информации, связанной с ним
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        String id = req.getParameter("id");
        if (id != null) {
            try {
                User user = (User) DAO.getObjectById(Long.valueOf(id), User.class);
                DAO.closeOpenedSession();
                if (user == null) {

                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                            ("Пользователь отсутвует в базе данных", null));

                } else {
                    DAO.deleteObject(user);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
                }

            } catch (NumberFormatException e) {

                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                        (e.getMessage(), null));
            }
        }
    }
}
