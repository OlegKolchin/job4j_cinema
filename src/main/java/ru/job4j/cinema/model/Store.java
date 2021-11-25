package ru.job4j.cinema.model;

import java.util.List;

public interface Store {
    void save(Account account);

    void save(Ticket ticket);

    void deleteAccount(String email);

    void deleteTicket(int row, int cell);

    List<Account> findAllAccounts();

    List<Ticket> findAllTickets();

    Account findAccountByEmail(String email);

    Ticket findTicket(int row, int cell);

}
