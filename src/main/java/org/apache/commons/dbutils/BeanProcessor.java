package org.apache.commons.dbutils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * The sourceCode comes from Apache
 * @author ygh 2016年12月27日
 *         <p>
 *         <code>BeanProcessor</code> matches column names to bean property names and convert the
 *         <code>ResulSet</code> columns into objects for these bean properties. Subclasses can
 *         override the methods in the processing chain to customize behavior
 *         </p>
 * 
 *         <p>
 *         The class is thread safe
 *         </p>
 * 
 * 
 */
public class BeanProcessor {

    /**
     * Special array value used by <code>mapColumnsToProperties</code> that indicates there is no
     * bean properties that matches a column from a <code>ResultSet</code>
     */
    private static final int PROPERTY_NOT_FOUND = -1;

    /**
     * Set a bean's primitive properties to these defaults when SQL NULL is returned. There are the
     * same as the defaults that ResultSet get* method return in the event of a NULL column
     */
    private static final Map<Class<?>, Object> primitiveDefault = new HashMap<Class<?>, Object>();

    /**
     * ServiceLoader to find <code>ColumnHandler</code> implementations on the classpath. The
     * iterator for this is lazy and each time <code>iterator()</code> is called.
     */
    private static final ServiceLoader<ColumnHandler> columnHandlers = ServiceLoader
            .load(ColumnHandler.class);

    /**
     * ServiceLoad to find <code>PropertyHandler</code> implementations on the classpath. The
     * iterator for this is lazy and each time <code>iterator[]</code> is called.
     */
    private static final ServiceLoader<PropertyHandler> propertyHandlers = ServiceLoader
            .load(PropertyHandler.class);

    /**
     * ResultSet column to bean property name overrides.
     */
    private Map<String, String> columnToPropertyOverrides;

    static {
        primitiveDefault.put(Byte.class, Byte.valueOf((byte) 0));
        primitiveDefault.put(Float.class, Float.valueOf(0f));
        primitiveDefault.put(Integer.class, Integer.valueOf(0));
        primitiveDefault.put(Long.class, Long.valueOf(0L));
        primitiveDefault.put(Double.class, Double.valueOf(0d));
        primitiveDefault.put(Short.class, Short.valueOf((short) 0));
        primitiveDefault.put(Boolean.class, Boolean.FALSE);
        primitiveDefault.put(Character.class, Character.valueOf((char) 0));
    }

    /**
     * Constructor for BeanProcessor
     */
    public BeanProcessor() {
        this(new HashMap<String, String>());
    }

    /**
     * Constructor for BeanProcessor with configured with column to property name overrides.
     * 
     * @param columnToPropertyOverrides The ResultSet column to property name overrides
     */
    public BeanProcessor(Map<String, String> columnToPropertyOverrides) {
        super();
        if (columnToPropertyOverrides == null) {
            throw new IllegalArgumentException("columnToPropertyOverrides map can not be null");
        }
        this.columnToPropertyOverrides = columnToPropertyOverrides;
    }

    /**
     * Convert a <code>ResultSet</code> into a JavaBean. This implementation users reflection and
     * <code>BeanInfo</code> classes to match column names to property names. Properties are matched
     * to columns based on several factors. <br/>
     * <ol>
     * <li>
     * The class has writable property with same name as column. The name comparison is case
     * insensitive.</li>
     * 
     * <li>
     * The column type can be converted to property's set method parameter type with a ResultSet
     * get* method. If the conversion fails (ie. the property was an int and the column was a
     * Timestamp) an SQLExeception will be thrown.
     * <li/>
     * <ol>
     * 
     * <p>
     * Primitive bean properties will be set their defaults when SQL NULL is returned from
     * <code>ResultSet</code> Numeric fields are set to 0 and booleans are set to false. Object
     * properties are set to <code>null</code> when SQL NULL is returned. This is same behavior same
     * as the <code>ResultSet</code> get* method
     * </p>
     * 
     * @param <T> The type of bean to create.
     * @param rs The <code>ResultSet</code> that supplies the bean data.
     * @param type The Class from which to create the bean instance.
     * @return The newly create bean.
     * @throws SQLException If a database access error occurs.
     */
    public <T> T toBean(ResultSet rs, Class<? extends T> type) throws SQLException {
        T bean = this.newInstance(type);
        return this.polulateBean(rs, bean);
    }

    /**
     * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans. This implementation
     * use reflection and <code>BeanInfo</code> classes to match the column names to property names.
     * Properties are matched to columns based on several factors: <br/>
     * <ol>
     * <li>
     * The class has a writable property with the same name as a column name. The name comparison is
     * case insensitive</li>
     * 
     * <li>
     * The column type can be converted to the property's set method parameter type with a
     * ResultSet.get*. If the conversion fails(ie.the property was an int and the column was a
     * Timestamp)an SQLException will is thrown</li>
     * </ol>
     * 
     * <p>
     *  Primitive bean will be set to their defaults when SQL NULL is returned from <code>ResultSet</code>
     *  Numeric fields are set to 0 and booleans are set false. Object bean will be set <code>null</code> when
     *  SQL NULL is returned. This is same behavior as the <code>ResultSet</code> get* method.
     * </p>
     * 
     * @param <T> The type of bean to create.
     * @param rs The ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @return the newly created List of bean
     * @throws SQLException If a database access error occurs.
     */
    public <T> List<T> toBeanList(ResultSet rs, Class<? extends T> type) throws SQLException {
        List<T> results = new ArrayList<T>();
        if(!rs.next()){
            return results;
        }
        PropertyDescriptor[] props = this.propertyDescriptors(type);
        int[] columnToProperty = this.mapColumnsToProperties(props, rs.getMetaData());
        do {
            results.add(this.createBean(rs, type, props, columnToProperty));
        } while (rs.next());
        return results;
    }
    
    /**
     * Create a new object and initialize its from the ResultSet
     * 
     * @param <T> The type bean to create
     * @param rs The ResultSet that supplies bean data
     * @param type The bean type
     * @param props The property descriptor
     * @param columnToProperty An array the columns match to properties
     * @return A initialized object
     * @throws SQLException If a database access error occurs.
     */
    private <T> T createBean(ResultSet rs,Class<T> type,PropertyDescriptor[]props,int[] columnToProperty) throws SQLException{
        T bean = this.newInstance(type);
        return this.polulateBean(rs, bean, props, columnToProperty);
    }

    /**
     * Initializes the fields of provided from the ResultSet
     * 
     * @param <T> The type of bean
     * @param rs The result set
     * @param bean The bean to be populated
     * @return An initialized Object
     * @throws SQLException If a database access error occurs;
     */
    public <T> T polulateBean(ResultSet rs, T bean) throws SQLException {
        PropertyDescriptor[] props = this.propertyDescriptors(bean.getClass());
        ResultSetMetaData rsmd = rs.getMetaData();
        int[] columnToProperty = this.mapColumnsToProperties(props, rsmd);

        return polulateBean(rs, bean, props, columnToProperty);
    }

    /**
     * This method populates a bean from the ResultSet based upon underlying meta-data
     * 
     * @param <T> The type of bean
     * @param rs The ResultSet
     * @param bean The bean to be populated
     * @param props The property descriptors
     * @param columnToProperty The Array column index to property index
     * @return An initialized object
     * @throws SQLException If a database access error occurs
     */
    public <T> T polulateBean(ResultSet rs, T bean, PropertyDescriptor[] props, int[] columnToProperty)
            throws SQLException {
        for (int i = 1; i < columnToProperty.length; i++) {
            if (columnToProperty[i] == PROPERTY_NOT_FOUND) {
                continue;
            }
            PropertyDescriptor prop = props[columnToProperty[i]];
            Class<?> propType = prop.getPropertyType();
            Object value = null;
            if (propType != null) {
                value = this.processColumn(rs, i, propType);
            }
            if (value == null && propType.isPrimitive()) {
                value = primitiveDefault.get(propType);
            }
            this.callSetter(bean, prop, value);

        }
        return bean;
    }

    /**
     * Call setter method on the target object for the given property. If no setter method exists
     * for property, the mehtod will do nothing.
     * 
     * @param target The object to set object on.
     * @param prop The property to set
     * @param value The value to pass into the setter
     * @throws SQLException If an error occurs setting the property.
     */
    private void callSetter(Object target, PropertyDescriptor prop, Object value) throws SQLException {
        Method setter = getWriteMethod(target, prop, value);
        if (setter == null || setter.getParameterTypes().length != 1) {
            return;
        }
        try {
            Class<?> firstParam = setter.getParameterTypes()[0];
            for (PropertyHandler handler : propertyHandlers) {
                if (handler.match(firstParam, value)) {
                    value = handler.apply(firstParam, value);
                    break;
                }
            }
            // Don't call setter if the value object is not the right type
            if (this.isCompatibleType(value, firstParam)) {
                setter.invoke(target, new Object[] { value });
            } else {
                throw new SQLException("Can not set " + prop.getName() + ":compatible types, can not convert"
                        + value.getClass().getName() + "to " + firstParam.getName());
                // value cannot be null here because isCompatibleType allows null.
            }
        } catch (IllegalAccessException e) {
            throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new SQLException("Cannot set " + prop.getName() + ": " + e.getMessage());
        }
    }

    /**
     * ResultSet.getObject() returns an Integer object for INT column. The setter method for the
     * property might take an Integer or primitive int. The method returns true if value can be
     * successfully passed into the setter method. Remember,Method.invoke() handles the unwrapping
     * of Integer into an int.
     * 
     * @param value The value to passed into the setter method
     * @param type The setter's parameter type(not null)
     * @return boolean True if the value is compatible(null => true)
     */
    boolean isCompatibleType(Object value, Class<?> type) {
        if (value == null || type.isInstance(value) || matchesPrimitive(type, value.getClass())) {
            return true;
        }
        return false;
    }

    /**
     * Check whether a value is of the same primitive as <code>targetType</code>
     * 
     * @param targetType The primitive to target
     * @param valueType The value to match the primitive type
     * @return Whether the <code>valueType</code> can be coerced into <code>targetType</code>
     */
    private boolean matchesPrimitive(Class<?> targetType, Class<? extends Object> valueType) {
        if (!targetType.isPrimitive()) {
            return false;
        }
        try {
            Field typeField = valueType.getField("TYPE");
            Object primitiveValueType = typeField.get(valueType);
            if (primitiveValueType == targetType) {
                return true;
            }
        } catch (NoSuchFieldException e) {

        } catch (IllegalAccessException e) {

        }

        return false;
    }

    /**
     * Get the write method to use when setting {@code value} to {@code target}
     * 
     * @param target Object write object will be called.
     * @param prop BeanUtils informations
     * @param value The value will be processed to the write method
     * @return The {@link Method} to call on {@code target} to write {@code value} or {@code null}
     *         if there is no suitable write method.
     */
    protected Method getWriteMethod(Object target, PropertyDescriptor prop, Object value) {
        Method method = prop.getWriteMethod();
        return method;
    }

    /**
     * Convert a <code>ResultSet</code> column into an Object. Simple implements is could just call
     * <code>rs.getObject(index)</code> while more complex implements could perform type
     * manipulation to match the column's type to bean property type.
     * 
     * <p>
     * This implement call appropriate <code>ResultSet</code> getter method for given type to
     * perform the type convert If the propType does not matches one of the supported
     * <code>ResultSet</code> types, <code>getObject</code> will be called.
     * </p>
     * 
     * @param rs The <code>ResultSet</code> currently being processed. It is positioned valid row
     *        before being passed into method
     * 
     * @param index The current index being processed
     * @param propType The bean property type the column need to convert into
     * @return The object from the <code>ResultSet</code> at given column index after optional
     *         processing or <code>null</code> if the column value is SQL NULL
     * @throws SQLException If a database access error occurs.
     */
    protected Object processColumn(ResultSet rs, int index, Class<?> propType) throws SQLException {
        Object value = rs.getObject(index);
        if (!propType.isPrimitive() && value == null) {
            return null;
        }
        for (ColumnHandler handler : columnHandlers) {
            if (handler.match(propType)) {
                value = handler.apply(rs, index);
                break;
            }
        }
        return value;
    }

    /**
     * The positions in the returned array represent column numbers.The values stored at each
     * position represent the index in the <code>PropertyDescriptor[]</code> for bean property that
     * matches the column name. If no bean property was found for column, the position is set to
     * <code>PROPERTY_NOT_FOUND</code>
     * 
     * @param props The bean property descriptors
     * @param rsmd the <code>ResultSetMetaData</code> containing column information.
     * @param SQLException If a database access error occurs.
     * @return An int[] with column index to property index mappings. The 0th is meaningless because
     *         the JDBC column indexing start at 1.
     */
    protected int[] mapColumnsToProperties(PropertyDescriptor[] props, ResultSetMetaData rsmd)
            throws SQLException {
        int cols = rsmd.getColumnCount();
        int[] columnToProperty = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);
        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            if (columnName == null | columnName.length() == 0) {
                columnName = rsmd.getCatalogName(col);
            }
            String propertyName = columnToPropertyOverrides.get(columnName);
            if (propertyName == null) {
                propertyName = columnName;
            }
            for (int i = 0; i < props.length; i++) {
                if (propertyName.equals(props[i].getName())) {
                    columnToProperty[col] = i;
                    break;
                }

            }
        }
        return columnToProperty;
    }

    /**
     * Return a PropertyDescriptors for given Class
     * 
     * @param c The Class to retrieve PropertyDescriptors for.
     * @return A PropertyDescriptor[] describing the Class.
     * @throws SQLException If Introsepction fail
     */
    private PropertyDescriptor[] propertyDescriptors(Class<?> c) throws SQLException {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);
        } catch (IntrospectionException e) {
            throw new SQLException("Bean Introsepction fail:" + e.getMessage());
        }

        return beanInfo.getPropertyDescriptors();
    }

    /**
     * Factory method return a new instance of given Class. This is called at the start of bean
     * creation process and may be overrides to provide custom behavior like returning cache bean
     * instance
     * 
     * @param <T> The type of Object to create
     * @param c The Class to create object from.
     * @return A newly created object from the Class
     * @throws SQLException If create failed.
     */
    protected <T> T newInstance(Class<T> c) throws SQLException {
        try {
            return c.newInstance();
        } catch (InstantiationException e) {
            throw new SQLException("Cannot create " + c.getName() + ":" + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new SQLException("Cannot create " + c.getName() + ":" + e.getMessage());

        }
    }

}
