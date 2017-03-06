package org.apache.commons.dbutils.handlers.properties;

import org.apache.commons.dbutils.PropertyHandler;

public class StringEmumPropertyHandler implements PropertyHandler {

    public boolean match(Class<?> parameter, Object value) {
        return value instanceof String && parameter.isEnum();
    }

    @SuppressWarnings("unchecked")
    public Object apply(Class<?> parameter, Object value) {
        return Enum.valueOf(parameter.asSubclass(Enum.class), (String)value);
    }

}
