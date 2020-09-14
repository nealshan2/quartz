/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quartz.integrations.tests;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class JdbcQuartzH2Utilities {

    private static final Logger LOG = LoggerFactory
            .getLogger(JdbcQuartzH2Utilities.class);

    public static final String DATABASE_DRIVER_CLASS = "org.h2.Driver";
    public static final String DATABASE_CONNECTION_PREFIX;
    public static final String DATABASE_PORT = "8022";
    public static final String DATABASE_USERNAME = "sa";
    public static final String DATABASE_PASSWORD = "";

    private static final List<String> DATABASE_SETUP_STATEMENTS;
    private static final List<String> DATABASE_TEARDOWN_STATEMENTS;

    private final static Properties PROPS = new Properties();

    static {
//        DATABASE_CONNECTION_PREFIX = "jdbc:h2:~/test";
        DATABASE_CONNECTION_PREFIX = "jdbc:h2:tcp://localhost:" + DATABASE_PORT + "/~/test";

        PROPS.setProperty("user", DATABASE_USERNAME);
        PROPS.setProperty("password", DATABASE_PASSWORD);


        try {
            Class.forName(DATABASE_DRIVER_CLASS).newInstance();
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }

        List<String> setup = new ArrayList<>();
        String setupScript;
        try {
            InputStream setupStream = JdbcQuartzH2Utilities.class
                    .getClassLoader().getResourceAsStream("org/quartz/job/jdbcjobstore/tables_h2.sql");
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(setupStream, "US-ASCII"));
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = r.readLine();
                    if (line == null) {
                        break;
                    } else if (!line.startsWith("--")) {
                        sb.append(line).append("\n");
                    }
                }
                setupScript = sb.toString();
            } finally {
                setupStream.close();
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        for (String command : setupScript.split(";")) {
            if (!command.matches("\\s*")) {
                setup.add(command);
            }
        }
        DATABASE_SETUP_STATEMENTS = setup;


        List<String> tearDown = new ArrayList<>();
        String tearDownScript;
        try {
            InputStream tearDownStream = JdbcQuartzH2Utilities.class
                    .getClassLoader().getResourceAsStream("tables_h2_drop.sql");
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(tearDownStream, "US-ASCII"));
                StringBuilder sb = new StringBuilder();
                while (true) {
                    String line = r.readLine();
                    if (line == null) {
                        break;
                    } else if (!line.startsWith("--")) {
                        sb.append(line).append("\n");
                    }
                }
                tearDownScript = sb.toString();
            } finally {
                tearDownStream.close();
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        for (String command : tearDownScript.split(";")) {
            if (!command.matches("\\s*")) {
                tearDown.add(command);
            }
        }
        DATABASE_TEARDOWN_STATEMENTS = tearDown;


    }

    public static void createDatabase() throws SQLException {

        // TODO: delete and create h2 database

        Connection conn = DriverManager.getConnection(DATABASE_CONNECTION_PREFIX, PROPS);
        try {
            Statement statement = conn.createStatement();
            for (String command : DATABASE_SETUP_STATEMENTS) {
                statement.addBatch(command);
            }
            statement.executeBatch();
        } finally {
            conn.close();
        }
    }


    public static int triggersInAcquiredState() throws SQLException {
        int triggersInAcquiredState = 0;
        Connection conn = DriverManager.getConnection(DATABASE_CONNECTION_PREFIX, PROPS);
        try {
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery("SELECT count( * ) FROM QRTZ_TRIGGERS WHERE TRIGGER_STATE = 'ACQUIRED' ");
            while (result.next()) {
                triggersInAcquiredState = result.getInt(1);
            }
        } finally {
            conn.close();
        }
        return triggersInAcquiredState;
    }


    public static BigDecimal timesTriggered(String triggerName, String triggerGroup) throws SQLException {
        BigDecimal timesTriggered = BigDecimal.ZERO;
        Connection conn = DriverManager.getConnection(DATABASE_CONNECTION_PREFIX, PROPS);
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT TIMES_TRIGGERED FROM QRTZ_SIMPLE_TRIGGERS WHERE TRIGGER_NAME = ? AND TRIGGER_GROUP = ? ");
            ps.setString(1, triggerName);
            ps.setString(2, triggerGroup);
            ResultSet result = ps.executeQuery();
            result.next();
            timesTriggered = result.getBigDecimal(1);
        } finally {
            conn.close();
        }
        return timesTriggered;
    }

    public static void destroyDatabase() throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_CONNECTION_PREFIX, PROPS);
        try {
            Statement statement = conn.createStatement();
            for (String command : DATABASE_TEARDOWN_STATEMENTS) {
                statement.addBatch(command);
            }
            statement.executeBatch();
        } finally {
            conn.close();
        }

    }

//    static class H2ConnectionProvider implements ConnectionProvider {
//
//
//        public Connection getConnection() throws SQLException {
//            return DriverManager.getConnection(DATABASE_CONNECTION_PREFIX, PROPS);
//        }
//
//        public void shutdown() throws SQLException {
//            // nothing to do
//        }
//
//        @Override
//        public void initialize() throws SQLException {
//            // nothing to do
//        }
//    }

    private JdbcQuartzH2Utilities() {
        // not instantiable
    }

}
