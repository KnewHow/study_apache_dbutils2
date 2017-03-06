package org.apache.commons.dbutils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This is utility class to provide DataSource from C3p0 and is responsible for transaction.
 * 
 * @author ygh 2017年1月5日
 */
public class JdbcUtils {

    /**
     * The parameter used to stored <code>DataSource</code>
     */
    private static DataSource dataSource;

    /**
     * The parameter used to stored ThreadLocal
     */
    private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();

    /**
     * Providing DataSource from C3p0.Subclasses can override to provide more
     * <code>DataSource</code> sources.
     * 
     * @return The DataSource provided by C3p0
     */
    protected static DataSource getDataSource() {
        dataSource = new ComboPooledDataSource();
        return dataSource;
    }

    /**
     * If ThreadLocal has be put a <code>Connection</code>, get it and return, otherwise call
     * <code>DataSource.getConnection()</code>
     * 
     * @return The <code>Connection</code>
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        if (tl.get() == null) {
            return getDataSource().getConnection();
        }
        return tl.get();
    }

    /**
     * Start transaction.
     * 
     * @throws SQLException If ThreadLocak has <code>Connection</code> indicates transaction has
     *         been started.
     */
    public static void beginTransaction() throws SQLException {
        Connection connection = tl.get();
        if (connection != null) {
            throw new SQLException("A transaction " + tl.get().getClass().getName()
                    + "has been started,don't start again ");
        }
        connection = getDataSource().getConnection();
        connection.setAutoCommit(false);
        tl.set(connection);
    }

    /**
     * Commit transaction
     * @throws SQLException If <code>Connection</code> is null, indicating no transactions
     */
    public static void commitTransaction() throws SQLException {
        Connection connection = tl.get();
        if (connection == null) {
            throw new SQLException("没有事务不能提交");
        }
        connection = tl.get();
        connection.commit();
        connection.close();
        tl.remove();
    }

    /**
     * Rollback transaction
     * @throws SQLException If <code>Connection</code> is null, indicating no transactions
     */
    public static void rollback() throws SQLException {
        Connection connection = tl.get();
        if (connection == null) {
            throw new SQLException("没有事务不能回滚");
        }
        connection = tl.get();
        connection.rollback();
        connection.close();
        tl.remove();
    }

    /**
     * Release connection if it don't join transaction.
     * @param connection The <code>Connection</code> need to release 
     * @throws SQLException If a database access error occurs
     */
    public static void realeaseConnection(Connection connection) throws SQLException {
        Connection con = tl.get();// 获取当前线程的事务连接
        if (connection != con) {// 如果参数连接，与当前事务连接不同，说明这个连接不是当前事务，可以关闭！
            if (connection != null && !connection.isClosed()) {// 如果参数连接没有关闭，关闭之！
                connection.close();
            }
        }
    }
}
