package org.apache.commons.dbutils.handlers.columns;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ColumnHandler;
/**
 * 
 * @author ygh
 * 2016年12月28日
 */
public class StringColumnHandler implements ColumnHandler {

    public boolean match(Class<?> propType) {
        return propType.equals(String.class);
    }

    public Object apply(ResultSet rs, int columnIndex) throws SQLException {
        return String.valueOf(rs.getString(columnIndex));
    }

}
