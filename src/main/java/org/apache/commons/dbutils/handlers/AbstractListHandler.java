package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
/**
 * Abstract class that simplify development of <code>ResultSetHandler</code> class
 * that convert <code>ResultSet</code> into <code>List</code>
 * @author ygh
 * 2017年1月7日
 * @param <T> The target <code>List</code> generic type
 */
public abstract class AbstractListHandler<T> implements ResultSetHandler<List<T>> {

    /**
     * Whole <code>ResultSet</code> handler. It produce <code>List</code> as result
     * To convert individual rows into java objects it uses <code>handleRow</code> method
     * @param rs The <code>ResultSet</code> to process, and its next() should not be caller
     * before processing this method
     * @return a list of all rows in result set
     * @throws SQLException If a database access error occurs.
     */
    public List<T> handle(ResultSet rs) throws SQLException {
        List<T> rows = new ArrayList<T>();
        while(rs.next()){
            rows.add(handleRow(rs));
        }
        return rows;
    }
    
    /**
     * Row handler. Method convert current row into some java objects
     * @param rs
     * @return
     * @throws SQLException
     */
    protected abstract T handleRow(ResultSet rs) throws SQLException;

}
