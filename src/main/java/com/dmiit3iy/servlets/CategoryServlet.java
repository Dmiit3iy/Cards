package com.dmiit3iy.servlets;

import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.ResponseResult;
import com.dmiit3iy.model.Category;
import com.dmiit3iy.model.User;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.List;

@WebServlet("/categories")
public class CategoryServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * •	post – осуществляет добавление новой категории для пользователя с заданным id
     * в базу данных(категорию посылать Jason., но без User, а аргументом посылать User.id
     * и потом на сервере проставлять объект User в категорию)
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
        String s = req.getParameter("id");
        String s2 = req.getParameter("category");
        try (BufferedReader reader = req.getReader()) {
            User user = (User) DAO.getObjectById(Long.valueOf(s), User.class);
            Category category = new Category(s2,user);
            DAO.closeOpenedSession();
            DAO.addObject(category);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, user));
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));

        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().println("Error " + e.getMessage());
        }
    }

    /**
     * get – осуществляет получение всех категорий для заданного id пользователя,
     * получение категории по ее id
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");

        String s = req.getParameter("id");
        String s2 = req.getParameter("catid");
        try {
            if (s != null && s2 == null) {

                Long l = Long.valueOf(s);
                User user = (User) DAO.getObjectById(l, User.class);
                if (user != null) {
                    List<Category> arrayList = user.getCategories();
                    String msg = objectMapper.writeValueAsString(arrayList);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, msg));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>("Отсутсвует пользователь с таким ID", null));
                }
            }
            if (s2 != null && s == null) {
                Long l2 = Long.valueOf(s2);
                Category category = (Category) DAO.getObjectById(l2, Category.class);
                if (category != null) {
                    String msg = objectMapper.writeValueAsString(category);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, msg));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>("Отсутсвует категория с таким ID", null));
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
        }
    }

    /**
     * put – осуществляет обновление категории по ее id
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");

        try (BufferedReader reader = req.getReader()) {

            Category categoryNew = objectMapper.readValue(reader, Category.class);
            Category categoryOld = (Category) DAO.getObjectById(categoryNew.getId(), Category.class);
            if (categoryOld != null) {

                User user = categoryOld.getUser();

                DAO.closeOpenedSession();

                categoryNew.setUser(user);

                DAO.updateObject(categoryNew);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, categoryNew));
                DAO.closeOpenedSession();
            } else {
                DAO.closeOpenedSession();
                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(),
                        new ResponseResult<>("Категория с таким ID отсутсвует", null));
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().println("Error " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        //String id = req.getParameter("id");
        String categoryName = req.getParameter("category");
       // if (id != null) {
            if (categoryName != null) {
            try {
               // Category category = (Category) DAO.getObjectById(Long.valueOf(id), Category.class);
                Category category = (Category) DAO.getObjectByParam("name",categoryName,Category.class);
                DAO.closeOpenedSession();
                if (category == null) {
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                            ("Категория отсутвует в базе данных", null));

                } else {
                    DAO.deleteObject(category);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null,category));
                }

            } catch (NumberFormatException e) {

                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                        (e.getMessage(), null));
            }
        }
    }

}
