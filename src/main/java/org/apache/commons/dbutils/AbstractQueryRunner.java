package org.apache.commons.dbutils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;

import javax.sql.DataSource;

/**
 * The base class for QueryRunner & AsyncQueryRunner. This class is thread safe
 * 
 * @author ygh 2017年1月15日
 */
public abstract class AbstractQueryRunner {

    /**
     * Is {@link ParameterMetaData#getParameterTypeName(int)}broken(have we tried it yet)?
     */
    private volatile boolean pmdKnownBroken = false;

    /**
     * The <code>DataSource</code> to retrieve connections from.
     */
    protected final DataSource ds;

    /**
     * Configuration to use when preparing statements
     */
    private final StatementConfiguration stmtConfig;

    /**
     * Default constructor, sets pmkKnownBroken false, ds to null, and stmtConfig null.
     */
    public AbstractQueryRunner() {
        this.ds = null;
        this.stmtConfig = null;
    }

    /**
     * Constructor to control the use of <code>ParameterMetaData</code>
     * 
     * @param pmdKnownBroken Some drivers don't support
     *        {@link ParameterMetaData#getParameterTypeName(int)}; if <code>pmkKnownBroken</code> is
     *        set true, we won't even try it; if false, we'll try it, and if it breaks, we'll
     *        remember not to use it again
     */
    public AbstractQueryRunner(boolean pmdKnownBroken) {
        this.pmdKnownBroken = pmdKnownBroken;
        this.ds = null;
        this.stmtConfig = null;
    }

    /**
     * Constructor to provide a <code>DataSource</code>. Methods that do not take a
     * <code>Connection</code> parameter will retrieve connections from this <code>DataSource</code>
     * 
     * @param ds The <code>DataSource</code> to retrieve connections from
     */
    public AbstractQueryRunner(DataSource ds) {
        this.ds = ds;
        this.stmtConfig = null;
    }

    /**
     * Constructor for QueryRunner that take a <code>StatementConfiguration</code> to configure
     * statements when preparing them.
     * 
     * @param stmtConfig The configuration to apply to statements when they are prepared
     */
    public AbstractQueryRunner(StatementConfiguration stmtConfig) {
        this.ds = null;
        this.stmtConfig = stmtConfig;
    }

    /**
     * Constructor to provide a <code>DataSource</code> and control the use of
     * <code>ParameterMetaData</code>. Methods that do not take a <code>Connections</code> will
     * retrieve connections from this <code>DataSource</code>
     * 
     * @param ds The <code>DataSurce</code> to retrieve connections from
     * @param pmdKnownBroken Some drivers don't support
     *        {@link ParameterMetaData#getParameterTypeName(int)}; if the
     *        <code>pmdKnownBroken</code> is set true, we will not try it, if it is set false, we
     *        will try it, and if it breaks, we'll remember not to try it again.
     */
    public AbstractQueryRunner(DataSource ds, boolean pmdKnownBroken) {
        this.pmdKnownBroken = pmdKnownBroken;
        this.ds = ds;
        this.stmtConfig = null;
    }

    /**
     * Constructor to provide a <code>DataSource</code> to use and
     * <code>StatementConfiguration</code>. Methods that don't take <code>Connection</code> will
     * retrieve connections from this <code>DataSource</code>
     * 
     * @param ds The <code>DataSource</code> to retrieve connections from
     * @param stmtConfig The configuration to apply to statements when they are prepared
     */
    public AbstractQueryRunner(DataSource ds, StatementConfiguration stmtConfig) {
        this.ds = ds;
        this.stmtConfig = stmtConfig;
    }

    /**
     * Constructor to provide a <code>DataSource</code> and a <code>pmdKnownBroken</code> and a
     * <code>StatementConfiguration</code> Methods that don't take connections will retrieve
     * connections from this <code>DataSource</code>
     * 
     * @param ds The <code>DataSource</code> to retrieve connections from
     * @param pmdKnownBroken Some drivers don't support
     *        {@link ParameterMetaData#getParameterTypeName(int)}; if the
     *        <code>pmdKnownBroken</code> is set true, we will not try it, if it is set false, we
     *        will try it, and if it breaks, we'll remember not to try it again.
     * @param stmtConfig The configuration to apply to statement when they are prepared
     */
    public AbstractQueryRunner(DataSource ds, boolean pmdKnownBroken, StatementConfiguration stmtConfig) {
        this.pmdKnownBroken = pmdKnownBroken;
        this.ds = ds;
        this.stmtConfig = stmtConfig;
    }

    /**
     * Return the <code>DataSource</code> this runner is using <code>QueryRunner</code> methods
     * always call this method to get the <code>DataSource</code> so subclass can provide specified
     * behavior.
     * 
     * @return The <code>DataSource</code> the running is using
     */
    public DataSource getDataSource() {
        return ds;
    }

    /**
     * /** Some drivers don't support {@link ParameterMetaData#getParameterType(int) }; if
     * <code>pmdKnownBroken</code> is set to true, we won't even try it; if false, we'll try it, and
     * if it breaks, we'll remember not to use it again.
     *
     * @return the flag to skip (or not) {@link ParameterMetaData#getParameterType(int) }
     */
    public boolean isPmdKnownBroken() {
        return pmdKnownBroken;
    }

    /**
     * Factory method that creates and initializes a <code>PreparedStatement</code> object for the
     * given SQL. <code>QueryRunner</code> methods always call this method to prepare statements for
     * them. Subclasses can override this method to provide special PreparedStatement configuration
     * if needed. This implementation simply calls <code>conn.prepareStatement(sql)</code>.
     *
     * @param conn The <code>Connection</code> used to create the <code>PreparedStatement</code>
     * @param sql The SQL statement to prepare.
     * @return An initialized <code>PreparedStatement</code>.
     * @throws SQLException if a database access error occurs
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {

        PreparedStatement ps = conn.prepareStatement(sql);
        configureStatement(ps);
        return ps;
    }

    /**
     * Factory method that creates and initializes a <code>PreparedStatement</code> object for the
     * given SQL. <code>QueryRunner</code> methods always call this method to prepare statements for
     * them. Subclasses can override this method to provide special PreparedStatement configuration
     * if needed. This implementation simply calls
     * <code>conn.prepareStatement(sql, returnedKeys)</code> which will result in the ability to
     * retrieve the automatically-generated keys from an auto_increment column.
     *
     * @param conn The <code>Connection</code> used to create the <code>PreparedStatement</code>
     * @param sql The SQL statement to prepare.
     * @param returnedKeys Flag indicating whether to return generated keys or not.
     *
     * @return An initialized <code>PreparedStatement</code>.
     * @throws SQLException if a database access error occurs
     * @since 1.6
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql, int returnedKeys)
            throws SQLException {

        PreparedStatement ps = conn.prepareStatement(sql, returnedKeys);
        configureStatement(ps);
        return ps;
    }

    private void configureStatement(Statement stmt) throws SQLException {

        if (stmtConfig != null) {
            if (stmtConfig.isFetchDirectionSet()) {
                stmt.setFetchDirection(stmtConfig.getFetchDirection());
            }

            if (stmtConfig.isFetchSizeSet()) {
                stmt.setFetchSize(stmtConfig.getFetchSize());
            }

            if (stmtConfig.isMaxFieldSizeSet()) {
                stmt.setMaxFieldSize(stmtConfig.getMaxFieldSize());
            }

            if (stmtConfig.isMaxRowsSet()) {
                stmt.setMaxRows(stmtConfig.getMaxRows());
            }

            if (stmtConfig.isQueryTimeoutSet()) {
                stmt.setQueryTimeout(stmtConfig.getQueryTimeout());
            }
        }
    }

    /**
     * Factory method that creates and initializes a <code>CallableStatement</code> object for the
     * given SQL. <code>QueryRunner</code> methods always call this method to prepare callable
     * statements for them. Subclasses can override this method to provide special CallableStatement
     * configuration if needed. This implementation simply calls <code>conn.prepareCall(sql)</code>.
     *
     * @param conn The <code>Connection</code> used to create the <code>CallableStatement</code>
     * @param sql The SQL statement to prepare.
     * @return An initialized <code>CallableStatement</code>.
     * @throws SQLException if a database access error occurs
     */
    protected CallableStatement prepareCall(Connection conn, String sql) throws SQLException {

        return conn.prepareCall(sql);
    }

    /**
     * Factory method that creates and initializes a <code>Connection</code> object.
     * <code>QueryRunner</code> methods always call this method to retrieve connections from its
     * DataSource. Subclasses can override this method to provide special <code>Connection</code>
     * configuration if needed. This implementation simply calls <code>ds.getConnection()</code>.
     *
     * @return An initialized <code>Connection</code>.
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    protected Connection prepareConnection() throws SQLException {
        if (this.getDataSource() == null) {
            throw new SQLException("QueryRunner requires a DataSource to be "
                    + "invoked in this way, or a Connection should be passed in");
        }
        return this.getDataSource().getConnection();
    }

    /**
     * Fill <code>PreparedStatement</code> replacement parameters with given objects
     * 
     * @param stmt The <code>PreparedStatement</code> to fill
     * @param params The Query replacement parameters, <code>null</code> is a valid value to pass
     *        in.
     * @throws SQLException If a database access error occurs.
     */
    @SuppressWarnings("rawtypes")
    public void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
        ParameterMetaData pmd = null;
        if (!this.pmdKnownBroken) {
            try {
                pmd = stmt.getParameterMetaData();
                if (pmd == null) {// can be returned by implementations that don't support the
                                  // method
                    pmdKnownBroken = true;
                } else {
                    int stmtCount = pmd.getParameterCount();
                    int paramsCount = params == null ? 0 : params.length;
                    if (stmtCount != paramsCount) {
                        throw new SQLException("Wrong number of parameters: expected " + stmtCount
                                + ", was given " + paramsCount);
                    }
                }
            } catch (SQLFeatureNotSupportedException ex) {
                pmdKnownBroken = true;
            }
        }
        // nothing to do here
        if (params == null) {
            return;
        }
        CallableStatement call = null;
        if (stmt instanceof CallableStatement) {
            call = (CallableStatement) stmt;
        }
        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                if (call != null && params[i] instanceof OutParameter) {
                    ((OutParameter) params[i]).register(call, i + 1);
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            } else {
                /*
                 * VARCHAR works with many drivers regardless of the actual column type. Oddly, NULL
                 * and OTHER don't work with Oracle's driver
                 */
                int sqlType = Types.VARCHAR;
                if (!pmdKnownBroken) {
                    /*
                     * It's not possible for pmkKnownBroken to change from true to false, (once
                     * true, always true) so pmd cannot be null here
                     */
                    try {
                        sqlType = pmd.getParameterType(i + 1);
                    } catch (SQLException e) {
                        pmdKnownBroken = true;
                    }
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }

    /**
     * Fill <code>PreparedStatement</code> replacement parameters with the given object's bean and
     * properties
     * 
     * @param stmt The <code>Statement</code> to fill
     * @param bean a JavaBean object
     * @param properties A ordered array of properties; this gives the order to insert values in the
     *        statement
     * @throws SQLException If a database access error occurs
     */
    public void fillStatementWithBean(PreparedStatement stmt, Object bean, PropertyDescriptor[] properties)
            throws SQLException {
        Object[] params = new Object[properties.length];
        for (int i = 0; i < properties.length; i++) {
            Object value = null;
            Method readMethod = properties[i].getReadMethod();
            if (readMethod == null) {
                throw new RuntimeException("No read method for bean property " + bean.getClass() + " "
                        + properties[i].getName());
            }
            try {
                value = readMethod.invoke(bean, new Object[0]);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Couldn't invoke method: " + readMethod, e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Couldn't invoke method: " + readMethod, e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Couldn't invoke method: " + readMethod, e);
            }
            params[i] = value;
        }
        fillStatement(stmt, params);
    }

    /**
     * Fill <code>PreparedStatement</code> replacement parameters with the given object's bean
     * property values
     * 
     * @param stmt The <code>PreparedStatement</code> to fill
     * @param bean A JavaBean object
     * @param propertyNames An ordered array of property names(these should match the
     *        getters/setters); the gives the order to insert values in the statement
     * @throws SQLException If a database access error occurs
     */
    public void fillStatementWithBean(PreparedStatement stmt, Object bean, String... propertyNames)
            throws SQLException {
        PropertyDescriptor[] descriptors = null;

        try {
            descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException("Could't introspect bean " + bean.getClass().toString(), e);
        }
        PropertyDescriptor[] stored = new PropertyDescriptor[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = propertyNames[i];
            if (propertyName == null) {
                throw new NullPointerException("PropertyName can't be null: " + i);
            }
            boolean found = false;
            for (int j = 0; j < descriptors.length; j++) {
                PropertyDescriptor descriptor = descriptors[i];
                if (propertyName.equals(descriptor.getName())) {
                    stored[i] = descriptor;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Could't find bean property: " + bean.getClass() + " "
                        + propertyName);
            }
        }

        fillStatementWithBean(stmt, bean, stored);
    }

    /**
     * Throws a new exception with a more information error message.
     * 
     * @param cause The original exception that will be chained to new exception when it's rethrown.
     * @param sql The query was executing when the exception happened
     * @param params The query replacement parameters; <code>null</code> is a valid value to pass it
     * @throws SQLException If a database access error occurs
     */
    protected void rethrow(SQLException cause, Object sql, Object... params) throws SQLException {
        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);
        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameter: ");
        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.deepToString(params));
        }
        SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());
        e.setNextException(cause);
        throw e;
    }

    /**
     * Wrap the <code>ResultSet</code> in a decorator before processing it. This implementation
     * returns the <code>ResultSet</code> it is given without any decoration.
     *
     * <p>
     * Often, the implementation of this method can be done in an anonymous inner class like this:
     * </p>
     *
     * <pre>
     * QueryRunner run = new QueryRunner() {
     *     protected ResultSet wrap(ResultSet rs) {
     *         return StringTrimmedResultSet.wrap(rs);
     *     }
     * };
     * </pre>
     *
     * @param rs The <code>ResultSet</code> to decorate; never <code>null</code>.
     * @return The <code>ResultSet</code> wrapped in some decorator.
     */
    protected ResultSet wrap(ResultSet rs) {
        return rs;
    }

    /**
     * Close a <code>Connection</code>. This implementation avoids closing if null and does
     * <strong>not</strong> suppress any exceptions. Subclasses can override to provide special
     * handling like logging.
     *
     * @param conn Connection to close
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    /**
     * Close a <code>Statement</code>. This implementation avoids closing if null and does
     * <strong>not</strong> suppress any exceptions. Subclasses can override to provide special
     * handling like logging.
     *
     * @param stmt Statement to close
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(Statement stmt) throws SQLException {
        DbUtils.close(stmt);
    }

    /**
     * Close a <code>ResultSet</code>. This implementation avoids closing if null and does
     * <strong>not</strong> suppress any exceptions. Subclasses can override to provide special
     * handling like logging.
     *
     * @param rs ResultSet to close
     * @throws SQLException if a database access error occurs
     * @since DbUtils 1.1
     */
    protected void close(ResultSet rs) throws SQLException {
        DbUtils.close(rs);
    }

}
