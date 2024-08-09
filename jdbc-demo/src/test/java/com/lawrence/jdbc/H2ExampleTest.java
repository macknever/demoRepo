package com.lawrence.jdbc;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class H2ExampleTest {

    private final H2Example h2 = new H2Example();

    @Test
    void queryShouldWork() throws SQLException {
        h2.showResult(h2.query("lawrence"));
    }

    @Test
    void insertShouldWork() throws SQLException {
        h2.insert("MJ");
        h2.showResult(h2.query("MJ"));
    }

    @Test
    void updateShouldWork() throws SQLException {
        h2.update("lawrence", "Yuansi");
        h2.showResult(h2.query("Yuansi"));
    }

    @Test
    void deleteShouldWork() throws SQLException {
        h2.insert("Levon");
        h2.showResult(h2.query("Levon"));
        h2.delete("Levon");
        h2.showResult(h2.query("Levon"));
    }

}
