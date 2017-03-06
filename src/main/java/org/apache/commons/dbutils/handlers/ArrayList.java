package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.RowProcessor;

/**
 * The <code>ResultSetHandler</code> implementation that convert the
 * <code>ResultSet</code> into a <code>List</code> or a <code>Object[]</code>
 * The class is thread sefe.  
 * @author ygh
 * 2017年1月7日
 */
public class ArrayList extends AbstractListHandler<Object[]> {

    
    /**
     * The <code>RowProcessor</code> implementation to use when converting
     * a <code>ResultSet</code> into <code>Objects</code>
     */
    private final RowProcessor convert;
    
    
    /**
     * Create a new instance of ArrayList with <code>BasicRowProcessor</code>
     * for conversions.
     */
    public ArrayList() {
        this(ArrayHandler.ROW_PROCESSOR);
    }


    /**
     * Create a new instance of ArrayList 
     * 
     * @param convert The <code>RowProcessor</code> implementation to use
     * when converting rows into <code>Object[]</code>s
     */
    public ArrayList(RowProcessor convert) {
        this.convert = convert;
    }

    
    /**
     * Create row's column into a <code>Object[]</code> 
     * @param rs The <code>ResultSet</code> to process
     * @return <code>Object[]</code>,never <code>null</code>
     * @throws If a database access error occurs;
     */
    @Override
    protected Object[] handleRow(ResultSet rs) throws SQLException {
        return this.convert.toArray(rs);
    }

}
