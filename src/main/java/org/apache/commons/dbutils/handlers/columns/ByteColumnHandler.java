package org.apache.commons.dbutils.handlers.columns;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ColumnHandler;

public class ByteColumnHandler implements ColumnHandler {

    public boolean match(Class<?> propType) {
        if(propType.equals(Byte.class) || propType.equals(Byte.TYPE)){
            return true;
        }
        return false;
    }

    public Object apply(ResultSet rs, int columnIndex) throws SQLException {
        return Byte.valueOf(rs.getByte(columnIndex));
    }

}
