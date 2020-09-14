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
package org.quartz.job.jdbcjobstore;

import org.quartz.integrations.tests.JdbcQuartzH2Utilities;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class JdbcQuartzTestUtilities {

    private static final String DATABASE_DRIVER_CLASS = JdbcQuartzH2Utilities.DATABASE_DRIVER_CLASS;
    private static final String DATABASE_CONNECTION_PREFIX = "jdbc:h2:mem:";
    private static final List<String> DATABASE_SETUP_STATEMENTS;
    private final static Properties PROPS = new Properties();

    static {
        PROPS.setProperty("user", JdbcQuartzH2Utilities.DATABASE_USERNAME);
        PROPS.setProperty("password", JdbcQuartzH2Utilities.DATABASE_PASSWORD);

        try {
            Class.forName(DATABASE_DRIVER_CLASS).newInstance();
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }

        List<String> setup = new ArrayList<String>();
        String setupScript;
        try {
            InputStream setupStream = EmbeddedConnectionProvider.class
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
    }

    public static void createDatabase(String name) throws SQLException {
        DBConnectionManager.getInstance().addConnectionProvider(name,
                new EmbeddedConnectionProvider(name));
    }

    public static void destroyDatabase(String name) throws SQLException {
        try {
            DriverManager.getConnection(
                    DATABASE_CONNECTION_PREFIX + name, PROPS).close();
        } catch (SQLException e) {
            if (!("Database 'memory:" + name + "' dropped.").equals(e.getMessage())) {
                throw e;
            }
        }
    }

    public static void shutdownDatabase(String name) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DATABASE_CONNECTION_PREFIX + name);
            connection.createStatement().execute("SHUTDOWN");
        } catch (SQLException e) {
            if (!("H2 system shutdown.").equals(e.getMessage())) {
                throw e;
            }
        } finally {
            connection.close();
        }
        try {
            Class.forName(DATABASE_DRIVER_CLASS).newInstance();
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        } catch (InstantiationException e) {
            throw new AssertionError(e);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    static class EmbeddedConnectionProvider implements ConnectionProvider {

        private final String databaseName;

        EmbeddedConnectionProvider(String name) throws SQLException {
            this.databaseName = name;
            Connection conn = DriverManager
                    .getConnection(DATABASE_CONNECTION_PREFIX + databaseName, PROPS);
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

        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(DATABASE_CONNECTION_PREFIX + databaseName, PROPS);
        }

        public void shutdown() throws SQLException {
            // nothing to do
        }

        public void initialize() throws SQLException {
            // nothing to do
        }
    }

    private JdbcQuartzTestUtilities() {
        // not instantiable
    }
}
