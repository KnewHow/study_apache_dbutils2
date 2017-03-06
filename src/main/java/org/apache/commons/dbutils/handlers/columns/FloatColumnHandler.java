package org.apache.commons.dbutils.handlers.columns;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ColumnHandler;
/**
 * 
 * @author ygh
 * 2016年12月28日
 */
public class FloatColumnHandler implements ColumnHandler {

    public boolean match(Class<?> propType) {
        if (propType.equals(Float.class) || propType.equals(Float.TYPE)) {
            return true;
        }
        return false;
    }

    public Object apply(ResultSet rs, int columnIndex) throws SQLException {
        return Float.valueOf(rs.getFloat(columnIndex));
    }

}
