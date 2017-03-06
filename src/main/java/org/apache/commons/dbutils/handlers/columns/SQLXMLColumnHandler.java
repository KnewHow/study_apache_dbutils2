package org.apache.commons.dbutils.handlers.columns;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;

import org.apache.commons.dbutils.ColumnHandler;
/**
 * 
 * @author ygh
 * 2016年12月28日
 */
public class SQLXMLColumnHandler implements ColumnHandler {

    public boolean match(Class<?> propType) {
        return propType.equals(SQLXML.class);
    }

    public Object apply(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getSQLXML(columnIndex);
    }

}
