package ru.job4j.cinema.model;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface Store {
    void save(Account account);

    void save(Ticket ticket) throws SQLIntegrityConstraintViolationException;

    void deleteAccount(String email);

    void deleteTicket(int row, int cell);

    List<Account> findAllAccounts();

    List<Ticket> findAllTickets();

    Account findAccountByEmail(String email);

    Ticket findTicket(int row, int cell);

}
