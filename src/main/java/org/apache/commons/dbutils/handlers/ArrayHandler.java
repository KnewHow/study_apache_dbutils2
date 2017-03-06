package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;

/**
 * 
 * @author ygh 2017年1月7日
 * 
 *         <code>ResultSetHandler</code> implementation that convert a <code>ResultSet</code> into
 *         <code>Object[]</code> This class is thread safe.
 */
public class ArrayHandler implements ResultSetHandler<Object[]> {

    /**
     * Singleton processor instance that handlers share to save memory. Notice the default scoping
     * to allow only classes in this package to use this instance
     */
    static final RowProcessor ROW_PROCESSOR = new BasicRowProcessor();

    /**
     * An empty array to return when no more column available in the <code>ResultSet</code>
     */
    private static final Object[] EMPTY_ARRAY = new Object[0];

    /**
     * The <code>RowProcessor</code> implementation to use when converting rows into arrays
     */
    private final RowProcessor convort;

    /**
     * Create a new instance of <code>ArrayHandler</code> using a <code>BaiscRowProcessor</code> for
     * convert
     */
    public ArrayHandler() {
        this(ROW_PROCESSOR);
    }

    /**
     * Create a new instance of <code>ArrayHandler</code>
     * 
     * @param convort The <code>RowProcessor</code> implementation to use when converting rows into
     *        arrays.
     */
    public ArrayHandler(RowProcessor convort) {
        this.convort = convort;
    }

    /**
     * Place the column values from first row in an <code>Object[]</code>
     * @param rs The <code>ResultSet</code> to process
     * @return An Object[]. If there are no rows in the <code>ResultSet</code>,
     * an empty array will be returned.
     * @throws If a database access error occurs.
     */
    public Object[] handle(ResultSet rs) throws SQLException {
        return rs.next() ? this.convort.toArray(rs) : EMPTY_ARRAY;
    }

}
