package org.apache.commons.dbutils;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 
 * @author ygh
 * 2016年12月27日
 * Interface to define how implementations can interact with column handling when constructing a bean from 
 * a {@link ResultSet}.ColumnsHandlers will do the work of retrieving data correctly from the <code>ResultSet</code>
 */
public interface ColumnHandler {
    
    /**
     * Test whether this <code>ColumnHanler</code> want to handle a column targetted for a value type matching
     * <code>propType</code>
     * @param propType The type of target parameter
     * @return true is this property handler can/wants to handle this value;false otherwise.
     */
    public boolean match(Class<?> propType);
    
    /**
     * Do the work need to retrieve and store a column from <code>ResultSet</code> into something of type
     * <code>propType</code> This method is called only if this handler responded <code>true</code> after
     * call {@link #match(Class)}
     * 
     * @param rs The result to get data from. This should be move to correct row already.
     * @param columnIndex The position of the column retrieve. 
     * @return The convert or original value if something does't work out.
     * @throws SQLException
     */
    public Object apply(ResultSet rs,int columnIndex) throws SQLException;
}
