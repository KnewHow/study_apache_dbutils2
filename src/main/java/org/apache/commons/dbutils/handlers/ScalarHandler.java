package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

/**
 * <code>ResultSetHandler</code> implementation that convert one <code>ResultSet</code> column into
 * an Object This class is thread safe.
 * 
 * @author ygh 2017年1月9日
 * @param <T>
 */
public class ScalarHandler<T> implements ResultSetHandler<T> {

    /**
     * The column number to retrieve
     */
    private final int columnIndex;

    /**
     * The column name to retrieve. Either columnName or columnIndex will be used but never both
     */
    private final String columnName;

    /**
     * Create a new instance of ScalarHandler. The first column will be returned from
     * <code>handler()</code>
     */
    public ScalarHandler() {
        this(1, null);
    }

    /**
     * Create a new instance of ScalarHandler
     * 
     * @param columnIndex The index of column to retrieve from <code>ResultSet</code>
     */
    public ScalarHandler(int columnIndex) {
        this(columnIndex, null);
    }

    /**
     * Create a new instance of ScalarHandler
     * 
     * @param columnName The name of column to retrieve from <code>ResultSet</code>
     */
    public ScalarHandler(String columnName) {
        this(1, columnName);
    }

    /**
     * Create a new instance of ScalarHanlder, the constructor is private
     * 
     * @param columnIndex The index of column to retrieve from <code>ResultSet</code>
     * @param columnName The name of column to retrieve from <code>ResultSet</code>
     */
    private ScalarHandler(int columnIndex, String columnName) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    @SuppressWarnings("unchecked")
    public T handle(ResultSet rs) throws SQLException {
        if(rs.next()){
            return (this.columnName == null) ? 
                    (T) rs.getObject(this.columnIndex) : 
                        (T) rs.getObject(columnName);
        }
        return null;
        
    }

}
