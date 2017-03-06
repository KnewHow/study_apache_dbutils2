package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;

/**
 * The <code>ResultSetHandler</code> implementation to convert a <code>ResultSet</code> into a
 * JavaBean
 * The class is thread safe.
 * 
 * @author ygh 2017年1月7日
 * @param <T> The type of the bean to create.
 */
public class BeanHandler<T> implements ResultSetHandler<T> {

    /**
     * The <code>RowProcessor</code> implementation to use when converting a <code>ResultSet</code>
     * into JavaBeab
     */
    private final RowProcessor convert;

    /**
     * The Class of beans produced by the handler.
     */
    private final Class<? extends T> type;

    
    /**
     * Create a new instance of BeanHandler
     * @param type The Class that objects returned from <code>handle()</code>
     * are create from
     */
    public BeanHandler(Class<? extends T> type) {
        this(type, ArrayHandler.ROW_PROCESSOR);
    }

    /**
     * Create a new instance of BeanHanlder
     * 
     * @param type The Class that objects returned from <code>handle</code>
     * are created from.
     * @param convert The <code>RowProcessor</code> implementation to use when converting a <code>ResultSet</code>
     * into a JavaBean.
     */
    public BeanHandler(Class<? extends T> type, RowProcessor convert) {
        this.convert = convert;
        this.type = type;
    }

    /**
     * Convert the first row of the <code>ResultSet</code> into JavaBean with the <code>Class</code> given
     * in the constructor.
     * @param The <code>ResultSet</code> to process
     * @return An initialized JavaBean or <code>null</code> if there were no rows in <code>ResultSet</code>
     * @throws If a database access error occurs 
     */
    public T handle(ResultSet rs) throws SQLException {
        return rs.next() ? this.convert.toBean(rs, type) : null;
    }

}
