package com.lawrence.jdbc;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class TransactionExampleTest {

    @Test
    void transactionShouldWork() throws SQLException {
        TransactionExample t = new TransactionExample();
        t.checkEmployee("Lawrence");

        t.promote("Lawrence");
        t.checkEmployee("Lawrence");
        t.checkManager("Lawrence");
    }

    @Test
    void dbStayUntouchedIfTransactionFail() throws SQLException {
        TransactionExample t = new TransactionExample();
        t.checkEmployee("Lawrence");
        t.checkManager("Levon");

        t.promote("Ricky");
        t.checkEmployee("Lawrence");
        t.checkManager("Levon");
        t.checkManager("Ricky");

        t.checkEmployee("Ricky");

    }
}
