package ru.job4j.cinema.model;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DbStore implements Store {

    private final BasicDataSource pool = new BasicDataSource();

    private static final Logger LOG = LoggerFactory.getLogger(DbStore.class.getName());

    private DbStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new InputStreamReader(
                        DbStore.class.getClassLoader()
                                .getResourceAsStream("db.properties")
                )
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }
    public static Store instOf() {
        return Lazy.INST;
    }

    private static final class Lazy {
        private static final Store INST = new DbStore();
    }


    @Override
    public void save(Account account) {
        if (account.getId() == 0) {
            create(account);
        } else {
            update(account);
        }
    }

    private void create(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO account(username, email, phone) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    account.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    private void update(Account account) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE account SET username =  ?, email = ?,"
                     + " phone = ? where id = ?")) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPhone());
            ps.setInt(4, account.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    @Override
    public void save(Ticket ticket) {
        if (ticket.getId() == 0) {
            create(ticket);
        } else {
            update(ticket);
        }
    }

    private void create(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO ticket(session_id, hall_row, cell, account_id) VALUES (?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getAccountId());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    ticket.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    private void update(Ticket ticket) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE ticket SET session_id =  ?, hall_row = ?,"
                     + " cell = ?, account_id = ? where id = ?")) {
            ps.setInt(1, ticket.getSessionId());
            ps.setInt(2, ticket.getRow());
            ps.setInt(3, ticket.getCell());
            ps.setInt(4, ticket.getAccountId());
            ps.setInt(5, ticket.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    @Override
    public void deleteAccount(String email) {
        try (Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("DELETE FROM account WHERE email = ?")) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    @Override
    public void deleteTicket(int row, int cell) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM ticket WHERE hall_row = ? and cell = ?")) {
            ps.setInt(1, row);
            ps.setInt(2, cell);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    @Override
    public List<Account> findAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM account")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    accounts.add(new Account(it.getInt("id"), it.getString("username"),
                            it.getString("email"), it.getString("phone")));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return accounts;
    }

    @Override
    public List<Ticket> findAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM ticket")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    tickets.add(new Ticket(it.getInt("id"),
                            it.getInt("session_id"),
                            it.getInt("hall_row"), it.getInt("cell"),
                            it.getInt("account_id")));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return tickets;
    }

    @Override
    public Account findAccountByEmail(String email) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM account WHERE email = ?")
        ) {
            ps.setString(1, email);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Account(it.getInt("id"), it.getString("username"),
                            it.getString("email"), it.getString("phone"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return null;
    }

    @Override
    public Ticket findTicket(int row, int cell) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM ticket WHERE hall_row = ? and cell = ?")
        ) {
            ps.setInt(1, row);
            ps.setInt(2, cell);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Ticket(it.getInt("id"), it.getInt("session_id"),
                            it.getInt("hall_row"), it.getInt("cell"),
                            it.getInt("account_id"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        return null;
    }
}
