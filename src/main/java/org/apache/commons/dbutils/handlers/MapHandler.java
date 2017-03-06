package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.RowProcessor;

/**
 * <code>ResultSet</code> implementation that convert <code>ResultSet</code> first row into a
 * <code>Map</code>. The class is thread safe.
 * 
 * @author ygh 2017年1月8日
 */
public class MapHandler implements ResultSetHandler<Map<String, Object>> {

    /**
     * The <code>RowProcessor</code> implementation to use when converting rows into a
     * <code>Map</code>
     */
    private final RowProcessor convert;

    /**
     * create a new instance of <code>MapHandler</code> using a <code>BasicRowProcessor</code> for
     * conversion.
     */
    public MapHandler() {
        this(ArrayHandler.ROW_PROCESSOR);
    }

    /**
     * Create a new instance of <code>MapHandel</code>
     * 
     * @param convert The <code>RowProcessor</code> implementation to use when converting a rows
     *        into a <code>Map</code>
     */
    public MapHandler(RowProcessor convert) {
        this.convert = convert;
    }

    /**
     * Convert the first of the rows to a <code>Map</code> or null there were no rows in
     * the <code>ResultSet</code>
     * @param rs The <code>ResultSet</code> to process
     * @return A <code>Map</code> with values from the firth row or <code>null</code>
     * if there were no rows set in <code>ResultSet</code>
     */
    public Map<String, Object> handle(ResultSet rs) throws SQLException {
        return rs.next() ? this.convert.toMap(rs) : null;
    }

}
