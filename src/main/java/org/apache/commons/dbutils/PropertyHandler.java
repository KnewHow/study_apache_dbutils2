package org.apache.commons.dbutils;


/**
 * 
 * @author ygh
 * 2016年12月28日
 * 
 * The interface to define how implementations can interact with property handing when constructing a bean from
 * {@link java.sql.ResultSet}. PropertyHandlers do the work of coercing a value into the target type required 
 */
public interface PropertyHandler {

    /**
     * Test whether this  <code>PropertyHanlde</code> wants to handle setting <code>value</code> into something of type
     * parameter.
     * @param parameter The type of target parameter.
     * @param value The value to be set
     * @return True is this handler can/wants to handle this value; false otherwise.
     */
    public boolean match(Class<?> parameter,Object value);
    /**
     * Do the work required to store <code>value</code> into something of type <code>parameter</code>.This method is
     * called only if this handler responded <code>true</code> after a call to {@link #match(Class, Object)} 
     * @param parameter The type of target parameter
     * @param value The value to be set
     * @return The converted value or original value if something does't work out. 
     */
    public Object apply(Class<?> parameter,Object value);
}
