package org.apache.commons.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The class is superclass of all handlers
 * The method {@link #handle()} is defined is this class
 * @author ygh
 * 2016年12月27日
 * @param <T> The type needed to deal and return
 */
public interface ResultSetHandler<T> {

    /**
     * The subclasses will implement this method and provide more
     * return types
     * @param rs The <code>ResultSet</code> to handle. It has not been
     * touched before passing to this method.
     * @return The object need to return
     * @throws If a database access error occurs.
     */
    public T handle(ResultSet rs)throws SQLException;
}
