package com.lawrence.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class ConnectionPoolExampleTest {
    private final ConnectionPoolExample cp = new ConnectionPoolExample();

    ConnectionPoolExampleTest() throws SQLException {
    }

    @Test
    void queryShouldWork() throws SQLException {
        cp.showResult(cp.query("lawrence"));
    }

    @Test
    void insertShouldWork() throws SQLException {
        cp.insert("MJ");
        cp.insert("MJ");
        cp.insert("MJ");
        cp.insert("MJ");
        ResultSet rs = cp.query("MJ");
        cp.showResult(rs);
    }

    @Test
    void updateShouldWork() throws SQLException {
        cp.update("lawrence", "Yuansi");
        cp.showResult(cp.query("Yuansi"));
    }

    @Test
    void deleteShouldWork() throws SQLException {
        cp.insert("Levon");
        cp.showResult(cp.query("Levon"));
        cp.delete("Levon");
        cp.showResult(cp.query("Levon"));
    }

}
