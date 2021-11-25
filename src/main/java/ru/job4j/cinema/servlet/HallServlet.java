package ru.job4j.cinema.servlet;

import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.model.DbStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.job4j.cinema.model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HallServlet extends HttpServlet {
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        OutputStream output = resp.getOutputStream();
        String json = GSON.toJson(DbStore.instOf().findAllTickets());
        output.write(json.getBytes(StandardCharsets.UTF_8));
        output.flush();
        output.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (DbStore.instOf().findAccountByEmail(req.getParameter("email")) == null) {
            DbStore.instOf().save(new Account(0, req.getParameter("username"),
                    req.getParameter("email"), req.getParameter("phone")));
        }
        Account account = DbStore.instOf().findAccountByEmail(req.getParameter("email"));
        HttpSession session = req.getSession();
        session.setAttribute("session_id", account.getId());
        int row = Integer.parseInt(req.getParameter("row"));
        int cell = Integer.parseInt(req.getParameter("cell"));
        int sessionId = (int) session.getAttribute("session_id");
        int accountId = account.getId();
        DbStore.instOf().save(
                new Ticket(0, sessionId, row, cell, accountId)
        );
    }
}
