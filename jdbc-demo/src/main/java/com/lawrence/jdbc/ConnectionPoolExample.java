package com.lawrence.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPoolExample {

    private static final String tableName = "USERS";
    private static final String SELECT_PRE_STATEMENT = "SELECT * FROM " + tableName + " WHERE name = ?";
    private static final String INSERT_PRE_STATEMENT = "INSERT INTO " + tableName + " (name) VALUES (?)";
    private static final String UPDATE_PRE_STATEMENT = "UPDATE " + tableName + " SET name = ? WHERE name = ?";

    private static final String DELETE_PRE_STATEMENT = "DELETE FROM " + tableName + " WHERE name = ?";

    private static final String SHOW_ALL_STATEMENT = "SELECT * FROM " + tableName;
    final HikariDataSource ds;
    final Connection connection;

    public ConnectionPoolExample() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:;INIT=RUNSCRIPT FROM 'classpath:user.sql'");
        ds = new HikariDataSource(config);
        connection = ds.getConnection();
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public ResultSet query(final String name) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(SELECT_PRE_STATEMENT);
        ps.setString(1, name);
        return ps.executeQuery();
    }

    public void insert(final String name) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(INSERT_PRE_STATEMENT);
        ps.setString(1, name);

        ps.executeUpdate();

    }

    public void update(final String prevName, final String newName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(UPDATE_PRE_STATEMENT);
        ps.setString(2, prevName);
        ps.setString(1, newName);
        ps.executeUpdate();
    }

    public void delete(final String name) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(DELETE_PRE_STATEMENT);
        ps.setString(1, name);
        ps.executeUpdate();
    }

    public void showAll() throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SHOW_ALL_STATEMENT);
        ResultSet rs = ps.executeQuery();
        showResult(rs);
    }

    public void showResult(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println(rs.getInt("id") + " - " + rs.getString("name"));
        }
    }

}
