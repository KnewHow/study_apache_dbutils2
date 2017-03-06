package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.RowProcessor;
/**
 * <code>ResultSetHandler</code> implementation that converts whole
 * <code>ResultSet</code> into a <code>List</code> of <code>Map</code>s.
 * This class is thread safe.    
 * @author ygh
 * 2017年1月8日
 */
public class MapListHandler  extends AbstractListHandler<Map<String, Object>>{

    /**
     * The <code>RowProcessor</code> to use when converting whole <code>ResultSet</code>
     * into a <code>List</code> of <code>Map</code>
     */
    private final RowProcessor convert;
    
    
    /**
     * Create a new instance of MapListHandler using a
     * <code>BasicRowProcessor</code>
     */
    public MapListHandler() {
        this(ArrayHandler.ROW_PROCESSOR);
    }

    /**
     * Create a new instance of MapListHandler
     * @param convert The <code>RowProcessor</code> implementation to use
     * when converting whole <code>ResultSet</code> into a <code>List</code>
     * of <code>Map</code>
     */
    public MapListHandler(RowProcessor convert) {
        this.convert = convert;
    }


    /**
     * Convert the <code>ResultSet</code> row into a <code>Map</code> object
     * @param The <code>ResultSet</code> to process
     * @return A <code>Map</code> never <code>null</code>
     * @throws SQLException If a database access error occurs.
     */
    @Override
    protected Map<String, Object> handleRow(ResultSet rs) throws SQLException {
        return this.convert.toMap(rs);
    }

}
