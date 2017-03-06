package org.apache.commons.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author ygh 2016年12月27日 The interface implementation will convert <code>ResultSet</code> into
 *         objects. Implementations can entend <code>BasicRowProcessor</code> to protect themselves
 *         from changes to this interfance
 */
public interface RowProcessor {
    /**
     * Create an <code>Object[]</code> from the column values in one <code>ResultSet</code> row.The
     * <code>ResultSet</code> should be positioned on a valid row before passing it to this method. 
     * Implementations of this methods must not alter the row position of the
     * <code>ResultSet</code>
     * 
     * @param rs The <code>ResultSet</code> that supplies array date
     * @return the newly created array
     * @throws SQLException If a database access error occurs
     */
    public Object[] toArray(ResultSet rs) throws SQLException;

    /**
     * Create a JavaBean from the the column values in one <code>ResultSet</code> row
     * The <code>ResultSer</code> should be positioned on a valid row before passing it to the method.
     * Implementations of this methods must not alter the position of the <code>ResultSet</code>
     * @param <T> The type of JavaBean
     * @param rs The <code>ResultSet</code> that supplies JavaBean data
     * @param type Class from which to create the bean instance
     * @return The newly created JavaBean
     * @throws SQLException If a database access error occurs
     */
    public <T> T toBean(ResultSet rs,Class<? extends T> type) throws SQLException;
    
    /**
     * Create a <code>List</code> of JavaBeans from column values in one <code>ResultSet</code> row
     * The <code>ResultSet.next()</code> should <strong>not</strong> be called before passing this method
     * @param <T> The type of bean to create
     * @param rs The <code>ResultSet</code> that supplies bean data
     * @param type Class from which to create the bean instance
     * @return The newly created <code>List</code> of javaBeans.
     * @throws SQLException
     */
    public <T> List<T> toBeanList(ResultSet rs,Class<? extends T> type) throws SQLException;

    /**
     * Create a <code>Map</code> from column values in one <code>ResultSet</code> row.
     * The <code>ResultSet</code> should be positioned a valid row before passing this mehtod.
     * @param rs The <code>ResultSet</code> that supplies map data
     * @return The newly map created
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Object> toMap(ResultSet rs) throws SQLException;
}
