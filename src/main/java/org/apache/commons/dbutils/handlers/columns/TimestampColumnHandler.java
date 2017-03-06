package org.apache.commons.dbutils.handlers.columns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.dbutils.ColumnHandler;
/**
 * 
 * @author ygh
 * 2016年12月28日
 */
public class TimestampColumnHandler implements ColumnHandler {

    public boolean match(Class<?> propType) {
        return propType.equals(Timestamp.class);
    }

    public Object apply(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getTimestamp(columnIndex);
    }

}
