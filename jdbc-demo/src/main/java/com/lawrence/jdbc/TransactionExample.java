package com.lawrence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionExample {
    private static final String H2_MEM_URL = "jdbc:h2:mem:";
    private static final String INIT_RUNNING = "INIT=RUNSCRIPT FROM 'classpath:employee.sql'";
    private static final String URL_SEPARATOR = ";";

    private static final String EMPLOYEE = "EMPLOYEE";
    private static final String SELECT_PRE_STATEMENT_EMPLOYEE = "SELECT * FROM " + EMPLOYEE + " WHERE name = ?";
    private static final String INSERT_PRE_STATEMENT_EMPLOYEE =
            "INSERT INTO " + EMPLOYEE + " (name, occupation) VALUES (?,?)";
    private static final String UPDATE_PRE_STATEMENT_EMPLOYEE = "UPDATE " + EMPLOYEE + " SET name = ? WHERE name = ?";
    private static final String DELETE_PRE_STATEMENT_EMPLOYEE = "DELETE FROM " + EMPLOYEE + " WHERE name = ?";

    private static final String MANAGER = "MANAGER";
    private static final String SELECT_PRE_STATEMENT_MANAGER =
            "SELECT * FROM " + MANAGER + " WHERE name = ?";
    private static final String INSERT_PRE_STATEMENT_MANAGER =
            "INSERT INTO " + MANAGER + " (name, occupation) VALUES (?,?)";
    private static final String UPDATE_PRE_STATEMENT_MANAGER = "UPDATE " + MANAGER + " SET name = ? WHERE name = ?";
    private static final String DELETE_PRE_STATEMENT_MANAGER = "DELETE FROM " + MANAGER + " WHERE name = ?";

    private final Connection connection;
    private final PreparedStatement insertEmployeeStatement;
    private final PreparedStatement deleteEmployeeStatement;
    private final PreparedStatement insertManagerStatement;
    private final PreparedStatement deleteManagerStatement;

    private final PreparedStatement queryEmployee;
    private final PreparedStatement queryManager;

    public TransactionExample() throws SQLException {
        try {
            connection = DriverManager.getConnection(H2_MEM_URL + URL_SEPARATOR + INIT_RUNNING);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        insertEmployeeStatement = connection.prepareStatement(INSERT_PRE_STATEMENT_EMPLOYEE);
        deleteEmployeeStatement = connection.prepareStatement(DELETE_PRE_STATEMENT_EMPLOYEE);
        insertManagerStatement = connection.prepareStatement(INSERT_PRE_STATEMENT_MANAGER);
        deleteManagerStatement = connection.prepareStatement(DELETE_PRE_STATEMENT_MANAGER);
        queryEmployee = connection.prepareStatement(SELECT_PRE_STATEMENT_EMPLOYEE);
        queryManager = connection.prepareStatement(SELECT_PRE_STATEMENT_MANAGER);
    }

    /**
     * Promote an employee to be a manager
     *
     * @param name
     */
    public void promote(String name) throws SQLException {
        try {
            connection.setAutoCommit(false);
            // Check if the employee exists before attempting to delete
            deleteEmployeeStatement.setString(1, name);
            int rowsDeleted = deleteEmployeeStatement.executeUpdate();

            if (rowsDeleted == 0) {
                throw new SQLException("Employee with name '" + name + "' does not exist.");
            }
            insertManagerStatement.setString(1, name);
            insertManagerStatement.setString(2, "manager");
            deleteEmployeeStatement.executeUpdate();
            insertManagerStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Rollback failed after an error during promotion", rollbackEx);
            }
        }

    }

    public void checkEmployee(String name) throws SQLException {
        queryEmployee.setString(1, name);
        ResultSet rs = queryEmployee.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("This employee does not exist: " + name);
        } else {
            showResult(rs);
        }
    }

    public void checkManager(String name) throws SQLException {
        queryManager.setString(1, name);
        ResultSet rs = queryManager.executeQuery();
        showResult(rs);
    }

    public void showResult(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println(rs.getInt("id") + " - " + rs.getString("name"));
        }
    }

}
