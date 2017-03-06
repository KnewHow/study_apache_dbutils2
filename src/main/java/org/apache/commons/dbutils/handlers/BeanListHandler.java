package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;

/**
 * The <code>RowProcessor</code> implementation convert whole <code>ResultSet</code> into a
 * <code>List</code> of JavaBean. This class is thread safe.
 * 
 * @author ygh 2017年1月7日
 * @param <T> The type of JavaBean to create.
 */
public class BeanListHandler<T> implements ResultSetHandler<List<T>> {

    /**
     * The Class of bean produced by this handler.
     */
    private final Class<? extends T> type;

    /**
     * The <code>RowProcessor</code> implementation to use when converting whole <code>ResultSet</code>
     * into a <code>List</code> of JavaBean
     */
    private final RowProcessor convert;

    /**
     * Create a new instance of BeanListHanlder
     * 
     * @param type The Class that objects returned from <code>handle()</code> are created from.
     */
    public BeanListHandler(Class<? extends T> type) {
        this(type, ArrayHandler.ROW_PROCESSOR);
    }

    /**
     * Create a new instance of BeanListHandler
     * 
     * @param type The Class that objects returned from <code>handle（）</code> are created from.
     * @param convert The <code>RowProcessoer</code> implementation to use when converting whole
     *        <code>ResultSet</code> into <code>List</code> of JavaBean.
     */
    public BeanListHandler(Class<? extends T> type, RowProcessor convert) {
        this.type = type;
        this.convert = convert;
    }
    
    /**
     * Convert whole <code>ResultSet</code> into <code>List</code> of JavaBean with Class given
     * in the constructor.
     * @param rs The <code>ResultSet</code> to handle, and its next() should not call before passing this mehtod
     * @return A newly <code>List</code> of JavaBean
     * @throws If a database accerr error occurs.
     */
    public List<T> handle(ResultSet rs) throws SQLException {
        return this.convert.toBeanList(rs, type);
    }

}
