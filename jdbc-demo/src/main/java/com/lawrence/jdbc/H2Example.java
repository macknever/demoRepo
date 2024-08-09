package com.lawrence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class showcases how to connect to h2 database and also how to do simple CRUD
 */
public class H2Example {
    private static final String H2_MEM_URL = "jdbc:h2:mem:";
    private static final String INIT_RUNNING = "INIT=RUNSCRIPT FROM 'classpath:user.sql'";
    private static final String URL_SEPARATOR = ";";

    private static final String tableName = "USERS";
    private static final String SELECT_PRE_STATEMENT = "SELECT * FROM " + tableName + " WHERE name = ?";
    private static final String INSERT_PRE_STATEMENT = "INSERT INTO " + tableName + " (name) VALUES (?)";
    private static final String UPDATE_PRE_STATEMENT = "UPDATE " + tableName + " SET name = ? WHERE name = ?";

    private static final String DELETE_PRE_STATEMENT = "DELETE FROM " + tableName + " WHERE name = ?";

    private final Connection connection;

    public H2Example() {
        try {
            connection = DriverManager.getConnection(H2_MEM_URL + URL_SEPARATOR + INIT_RUNNING);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet query(final String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_PRE_STATEMENT);
        ps.setString(1, name);
        return ps.executeQuery();
    }

    public void insert(final String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_PRE_STATEMENT);
        ps.setString(1, name);
        ps.executeUpdate();
    }

    public void update(final String prevName, final String newName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_PRE_STATEMENT);
        ps.setString(2, prevName);
        ps.setString(1, newName);
        ps.executeUpdate();
    }

    public void delete(final String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_PRE_STATEMENT);
        ps.setString(1, name);
        ps.executeUpdate();
    }

    public void showResult(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println(rs.getInt("id") + " - " + rs.getString("name"));
        }
    }
}
