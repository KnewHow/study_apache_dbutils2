package org.apache.commons.dbutils;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * A collection of JDBC helper methods. This class is thread safe
 * 
 * @author ygh 2017年1月14日
 */
public class DbUtils {

    /**
     * Default constructor Utility classes should not hava a public or default constructor, but this
     * one preserves retro-compatibility
     */
    public DbUtils() {
        // do nothing
    }

    /**
     * Close a <code>Connection</code>, avoid closing if null.
     * 
     * @param conn The <code>Connection</code> need to close
     * @throws SQLException If a database access errors occurs
     */
    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null.
     * 
     * @param rs The <code>ResultSet</code> need to close
     * @throws SQLException If a database access error occurs
     */
    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null.
     * 
     * @param stmt The <code>Statement</code> need to close
     * @throws SQLException If a database access error occurs
     */
    public static void close(Statement stmt) throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }

    /**
     * Close a <code>Connection</code>, avoid closing if null and hide any SQLException that occur.
     * 
     * @param conn The <code>Connection</code> to close
     */
    public static void closeQuietly(Connection conn) {
        try {
            close(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Statement</code>, avoid closing if null and hide any SQLException that occur.
     * 
     * @param stmt The <code>Statement</code> to close
     */
    public static void closeQuietly(Statement stmt) {
        try {
            close(stmt);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>ResultSet</code>, avoid closing if null and hide any SQLException that occur.
     * 
     * @param rs The <code>ResultSet</code> to close
     */
    public static void closeQuietly(ResultSet rs) {
        try {
            close(rs);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Close a <code>Connection</code>, <code>Statement</code> and <code>Result</code>, avoid
     * closing if null and hide any SQLException that occurs
     * 
     * @param conn
     * @param stmt
     * @param rs
     */
    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {
        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(stmt);
            } finally {
                closeQuietly(conn);
            }
        }
    }

    /**
     * Commits a <code>Connection</code> then close it, avoid closing if null.
     * 
     * @param conn The <code>Connection</code> need to commit and close
     * @throws SQLException If a database access error occurs.
     */
    public static void commitAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.commit();
            } finally {
                close(conn);
            }
        }
    }

    /**
     * Commit a <code>Connection</code> then closing it, avoid closing if null and hide any
     * SQLException that occurs.
     * 
     * @param conn The <code>Connection </code> need to commit and close.
     */
    public static void commitAndCloseQuietly(Connection conn) {
        try {
            commitAndClose(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Rollback any changes made on the given connection
     * 
     * @param conn The <code>Connection</code> need to rollback. A null value is legal.
     * @throws SQLException If a database access error occurs
     */
    public static void rollback(Connection conn) throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    /**
     * Rollback any changes made on given <code>Connection</code> then close it, avoid closing if
     * null
     * 
     * @param conn The <code>Connection</code> need to rollback and close
     * @throws SQLException If a database access error occurs.
     */
    public static void rollbackAndClose(Connection conn) throws SQLException {
        if (conn != null) {
            try {
                conn.rollback();
            } finally {
                conn.close();
            }
        }
    }

    /**
     * Rollback ang changes made on given <code>Connection</code> then close it, avoid closing if
     * null and hide any SQLException that occurs
     * 
     * @param conn The <code>Connection</code> need to rollbacn and close.
     */
    public static void rollbackAndCloseQuietly(Connection conn) {
        try {
            rollbackAndClose(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

    /**
     * Print the stack trace for a SQLException to specified PrintWriter.
     * 
     * @param e The <code>SQLException</code> to print stack trace of
     * @param pw The <code>PrintWriter</code> to print to
     */
    public static void printStackTrace(SQLException e, PrintWriter pw) {
        SQLException next = e;
        while (next != null) {
            next.printStackTrace(pw);
            next = next.getNextException();
            if (next != null) {
                System.out.println("Next SQLException:");
            }
        }
    }

    /**
     * Print the stack trace for a SLException to SRDERR.
     * 
     * @param e The <code>SQLException</code> to print stack trace of
     */
    public static void printStackTrace(SQLException e) {
        printStackTrace(e, new PrintWriter(System.err));
    }

    /**
     * Print warning on a <code>Connection</code> to specified <code>PrintWriter</code>
     * 
     * @param conn The <code>Connection</code> print warning from
     * @param pw The <code>PrintWriter</code> to print to
     */
    public static void printWarings(Connection conn, PrintWriter pw) {
        if (conn != null) {
            try {
                printStackTrace(conn.getWarnings(), pw);
            } catch (SQLException e) {
                printStackTrace(e, pw);
            }
        }
    }

    /**
     * Print warnings on a <code>Connection</code> to STDERR
     * 
     * @param conn The <code>Connection</code> to print warning from
     */
    public static void printWarings(Connection conn) {
        printWarings(conn, new PrintWriter(System.err));
    }

    /**
     * Load and register a database driver class. If this success, it returns true, else it returns
     * false
     * 
     * @param driverClassName The class name of driver
     * @return true if success, otherwise false
     */
    public static boolean loadDriver(String driverClassName) {
        return loadDriver(DbUtils.class.getClassLoader(), driverClassName);
    }

    /**
     * Load and register a database driver class. If this success, it returns true, else it returns
     * false
     * 
     * @param classLoader The class loader used to load driver class
     * @param driverClassName The name of driver class
     * @return <code>true</code> if the driver was found, otherwise <code>false</code>
     */
    public static boolean loadDriver(ClassLoader classLoader, String driverClassName) {
        try {
            Class<?> loadClass = classLoader.loadClass(driverClassName);
            if (Driver.class.isAssignableFrom(loadClass)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Class<Driver> driverClass = (Class<Driver>) loadClass;
            Constructor<Driver> driverClassConstructor = driverClass.getConstructor();
            boolean isConstructorAccessible = driverClassConstructor.isAccessible();
            if (!isConstructorAccessible) {
                driverClassConstructor.setAccessible(true);
            }

            try {
                Driver driver = driverClassConstructor.newInstance();
                DriverManager.registerDriver(new DriverProxy(driver));
            } finally {
                driverClassConstructor.setAccessible(isConstructorAccessible);
            }

            return true;

        } catch (RuntimeException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Simple {@link Driver} proxy class that proxies a JDBC　Driver loaded dynamically
     * 
     * @author ygh 2017年1月15日
     */
    private static final class DriverProxy implements Driver {

        private boolean parentLoggerSupported = true;

        /**
         * The updated JDBC Driver loaded dynamically
         */
        private final Driver updated;

        public DriverProxy(Driver updated) {
            this.updated = updated;
        }

        public Connection connect(String url, Properties info) throws SQLException {
            return this.updated.connect(url, info);
        }

        public boolean acceptsURL(String url) throws SQLException {
            return this.updated.acceptsURL(url);
        }

        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return this.updated.getPropertyInfo(url, info);
        }

        public int getMajorVersion() {
            return this.updated.getMajorVersion();
        }

        public int getMinorVersion() {
            return this.updated.getMinorVersion();
        }

        public boolean jdbcCompliant() {
            return this.updated.jdbcCompliant();
        }

        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            if (this.parentLoggerSupported) {
                try {
                    Method method = updated.getClass().getMethod("getParentLogger", new Class[0]);
                    return (Logger) method.invoke(updated, new Object[0]);
                } catch (NoSuchMethodException e) {
                    parentLoggerSupported = false;
                    throw new SQLFeatureNotSupportedException(e);
                } catch (IllegalAccessException e) {
                    parentLoggerSupported = false;
                    throw new SQLFeatureNotSupportedException(e);
                } catch (InvocationTargetException e) {
                    parentLoggerSupported = false;
                    throw new SQLFeatureNotSupportedException(e);
                }
            }
            throw new SQLFeatureNotSupportedException();
        }

    }

}
