package com.dmiit3iy.servlets;

import com.dmiit3iy.DAO.DAO;
import com.dmiit3iy.dto.ResponseResult;
import com.dmiit3iy.model.Card;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cards")

public class CardServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();

    {
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * post – осуществляет добавление карточки пользователя по id категории
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
        String s1 = req.getParameter("category");
        String s2 = req.getParameter("question");
        String s3 = req.getParameter("answer");
        try (BufferedReader reader = req.getReader()) {
            if (s!=null&&s1==null) {
                Card card = objectMapper.readValue(reader, Card.class);
                Category category = (Category) DAO.getObjectById(Long.valueOf(s), Category.class);
                DAO.closeOpenedSession();
                card.setCategory(category);
                card.setCreationDate(LocalDateTime.now());
                DAO.addObject(card);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, card));
            }
            if (s==null&&s1!=null){
                Card card = new Card();
                card.setAnswer(s3);
                card.setQuestion(s2);
                Category category = (Category) DAO.getObjectByParam("name",s1, Category.class);
                DAO.closeOpenedSession();
                card.setCategory(category);
                card.setCreationDate(LocalDateTime.now());
                DAO.addObject(card);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, card));
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));

        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().println("Error " + e.getMessage());
        }
    }

    /**
     * get – осуществляет получение всех карточек для заданного id категории,
     * для заданного id пользователя, получение карточки  по ее id
     *
     * @param req
     * @param resp
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("utf-8");
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");

        String s = req.getParameter("catid");
        String s2 = req.getParameter("cardid");
        String s3 = req.getParameter("iduser");
        String s4 = req.getParameter("category");
        try {
            if (s == null && s2 != null && s3 == null && s4 == null) {
                Long l2 = Long.valueOf(s2);
                Card card = (Card) DAO.getObjectById(l2, Card.class);
                if (card != null) {
                    String msg = objectMapper.writeValueAsString(card);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, msg));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>("Отсутсвует категория с таким ID", null));
                }
            } else if (s != null && s2 == null && s3 == null && s4 == null) {

                Long l = Long.valueOf(s);
                Category category = (Category) DAO.getObjectById(l, Category.class);
                if (category != null) {
                    List<Card> arrayList = category.getCards();
                    String msg = objectMapper.writeValueAsString(arrayList);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, msg));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>("Отсутсвует категдория с таким ID", null));
                }
            } else if (s == null && s2 == null && s3 == null && s4 != null) {


                Category category = (Category) DAO.getObjectByParam("name", s4, Category.class);
                if (category != null) {
                    List<Card> arrayList = category.getCards();
                    String msg = objectMapper.writeValueAsString(arrayList);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, msg));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>("Отсутсвует категдория с таким ID", null));
                }
            } else if (s == null && s2 == null && s3 != null && s4 == null) {
                Long l = Long.valueOf(s3);
                User user = (User) DAO.getObjectById(l, User.class);
                if (user != null) {
                    List<Category> arrayList = user.getCategories();
                    List<Card> cardList = new ArrayList<>();
                    for (Category x : arrayList) {
                        List<Card> cards = x.getCards();
                        cardList.addAll(cards);
                    }
                    String msg = objectMapper.writeValueAsString(cardList);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, msg));
                    DAO.closeOpenedSession();
                } else {
                    DAO.closeOpenedSession();
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>("Отсутсвует категдория с таким ID", null));
                }
            } else {
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>("Ошибка в передаче параметров", null));
                DAO.closeOpenedSession();
            }
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(e.getMessage(), null));
        }
    }

    /**
     * put – осуществляет обновление карточки по ее id
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
        String id = req.getParameter("id");
        String question = req.getParameter("question");
        String answer = req.getParameter("answer");

        Card cardNew = (Card) DAO.getObjectById(Long.valueOf(id), Card.class);

        if (cardNew != null) {
            //DAO.closeOpenedSession();
            cardNew.setQuestion(question);
            cardNew.setAnswer(answer);
            DAO.updateObject(cardNew);
            this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, cardNew));
            DAO.closeOpenedSession();
        } else {
            DAO.closeOpenedSession();
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(),
                    new ResponseResult<>("Карточка с таким ID отсутствует", null));
        }


    }

    /**
     * delete – осуществляет удаление записи из базы данных по ее id
     *
     * @param req
     * @param resp
     * @throws IOException
     */

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        String id = req.getParameter("id");
        if (id != null) {
            try {
                Card card = (Card) DAO.getObjectById(Long.valueOf(id), Card.class);
                DAO.closeOpenedSession();
                if (card == null) {
                    resp.setStatus(400);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                            ("Карточка отсутвует в базе данных", null));
                } else {
                    DAO.deleteObject(card);
                    this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>(null, card));
                }
            } catch (NumberFormatException e) {

                resp.setStatus(400);
                this.objectMapper.writeValue(resp.getWriter(), new ResponseResult<>
                        (e.getMessage(), null));
            }
        }
    }
}

