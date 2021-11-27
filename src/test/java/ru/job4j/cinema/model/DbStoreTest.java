package ru.job4j.cinema.model;

import org.junit.Test;

import javax.servlet.ServletException;

import java.sql.SQLIntegrityConstraintViolationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DbStoreTest {

    @Test
    public void whenCreateAccount() {
        Store store = DbStore.instOf();
        Account account = new Account(0, "Vasiliy", "email", "+84343");
        store.save(account);
        assertThat(store.findAccountByEmail(account.getEmail()).getEmail(), is("email"));
    }

    @Test public void whenDeleteAccount() {
        Store store = DbStore.instOf();
        Account account = new Account(0, "Oleg", "email2", "+843434");
        store.save(account);
        store.deleteAccount(account.getEmail());
        assertThat(store.findAccountByEmail(account.getEmail()), is(nullValue()));
    }

    @Test public void whenCreateTicket() throws SQLIntegrityConstraintViolationException {
        Store store = DbStore.instOf();
        Account account = new Account(0, "Ivan", "email23", "+8434345");
        store.save(account);
        int accId = store.findAccountByEmail(account.getEmail()).getId();
        Ticket ticket = new Ticket(0, 1, 2, 3, accId);
        store.save(ticket);
        Ticket rsl = store.findTicket(2, 3);
        assertThat(rsl, notNullValue());
    }

    @Test public void whenDeleteTicket() throws SQLIntegrityConstraintViolationException {
        Store store = DbStore.instOf();
        Account account = new Account(0, "Ivan", "email233", "+84343453");
        store.save(account);
        int accId = store.findAccountByEmail(account.getEmail()).getId();
        Ticket ticket = new Ticket(0, 1, 2, 3, accId);
        store.save(ticket);
        store.deleteTicket(ticket.getRow(), ticket.getCell());
        Ticket rsl = store.findTicket(2, 3);
        assertThat(rsl, is(nullValue()));
    }
}