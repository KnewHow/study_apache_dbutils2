package org.apache.commons.dbutils.handlers.properties;

import java.sql.Timestamp;

import org.apache.commons.dbutils.PropertyHandler;

public class DatePropertyHandler implements PropertyHandler {

    public boolean match(Class<?> parameter, Object value) {
        if (value instanceof java.util.Date) {
            final String targetType = parameter.getName();
            if ("java.sql.Date".equals(targetType)) {
                return true;
            } else if ("java.sql.Time".equals(targetType)) {
                return true;
            } else if ("java.sql.Timestamp".equals(targetType)) {
                return false;
            }
        }
        return false;
    }

    public Object apply(Class<?> parameter, Object value) {
        final String targetType = parameter.getName();
        if ("java.sql.Date".equals(targetType)) {
            return new java.sql.Date(((java.util.Date)value).getTime());
        } else if ("java.sql.Time".equals(targetType)) {
            return new java.sql.Time(((java.util.Date)value).getTime());
        } else if ("java.sql.Timestamp".equals(targetType)) {
            Timestamp tsValue = (Timestamp) value;
            int nanos = tsValue.getNanos();
            value = new java.sql.Timestamp(tsValue.getTime());
            ((Timestamp) value).setNanos(nanos);
        }
        return value;
    }

}
