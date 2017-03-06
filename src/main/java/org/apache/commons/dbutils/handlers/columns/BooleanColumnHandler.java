package org.apache.commons.dbutils.handlers.columns;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ColumnHandler;

/**
 *
 * @author ygh
 * 2016年12月28日
 */
public class BooleanColumnHandler implements ColumnHandler{
    
    public boolean match(Class<?> propType) {
        if(propType.equals(Boolean.class)|| propType.equals(Boolean.TYPE)){
            return true;
        }
        return false;
    }

    public Object apply(ResultSet rs, int columnIndex) throws SQLException {
        return Boolean.valueOf(rs.getBoolean(columnIndex));
    }

}
