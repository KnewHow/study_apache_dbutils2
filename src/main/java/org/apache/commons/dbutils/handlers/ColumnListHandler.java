package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <code>ResultSetHandler</code> implementation that converts one <code>ResultSet</code> column into
 * a <code>List</code> of <code>Objects</code>
 * 
 * This class is thread safe.
 * 
 * 
 * @author ygh 2017年1月9日
 * @param <T> The type of the column
 */
public class ColumnListHandler<T> extends AbstractListHandler<T> {

    /**
     * The column index to retrieve
     */
    private final int columnIndex;

    /**
     * The column name to retrieve. Either columnName or columnIndex will be used, but never both.
     */
    private final String columnName;

    /**
     * Create a new instance of ColumnListHandler. The first column of each row will be returned
     * from <code>handler()</code>
     */
    public ColumnListHandler() {
        this(1, null);
    }

    /**
     * Create a new instance of ColumnListHandler
     * 
     * @param columnIndex The column index of column to retrieve from the <code>ResultSet</code>
     */
    public ColumnListHandler(int columnIndex) {
        this(columnIndex, null);
    }

    /**
     * Create a new instance of ColumnListHandler
     * 
     * @param columnName The column name of column to retrieve from the <code>ResultSet</code>
     */
    public ColumnListHandler(String columnName) {
        this(1, columnName);
    }

    /**
     * Create a new instance of ColumnListHandler, this constructor is private
     * 
     * @param columnIndex The index of column to retrieve from the <code>ResultSet</code>
     * @param columnName The name of column to retrieve from the <code>ResultSet</code>
     */
    private ColumnListHandler(int columnIndex, String columnName) {
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    /**
     * Return one <code>ResultSet</code> value as <code>Object</code>
     * 
     * @param rs The <code>ResultSet</code> to process
     * @return <code>Object</code>, never <code>null</code>
     * @throws SQLException If a database access error occurs.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected T handleRow(ResultSet rs) throws SQLException {
        return (this.columnName == null) ? (T) rs.getObject(this.columnIndex) : (T) rs
                .getObject(this.columnName);
    }

}
