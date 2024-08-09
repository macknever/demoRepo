package com.lawrence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:h2:mem:;INIT=RUNSCRIPT FROM 'classpath:user.sql'")) {
            System.out.printf("connected? " + connection.isValid(0));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
