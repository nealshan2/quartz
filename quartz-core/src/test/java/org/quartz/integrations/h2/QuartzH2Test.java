package org.quartz.integrations.h2;

import junit.framework.TestCase;

import java.sql.*;

public class QuartzH2Test extends TestCase {

    Connection conn = null;

    @Override
    protected void setUp() throws Exception {
        conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        conn.close();

    }

    public void testDbConnection() throws SQLException {
        assertNotNull(conn);

        PreparedStatement ps = conn.prepareStatement("select * from dual");
        boolean result = ps.execute();
        assertTrue(result);

        ps = conn.prepareStatement(" CREATE TABLE IF NOT EXISTS TEST(ID INT)");
        int ddlCount = ps.executeUpdate();
        assertEquals(0, ddlCount);

        ps = conn.prepareStatement(" INSERT INTO TEST(ID) VALUES (1);");
        int dmlCount = ps.executeUpdate();
        assertEquals(1, dmlCount);

        ps = conn.prepareStatement("select ID from test");
        ResultSet resultSet = ps.executeQuery();
        resultSet.next();
        assertEquals(1, resultSet.getInt("ID"));

    }
}
