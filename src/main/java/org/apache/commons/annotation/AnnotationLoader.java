package org.apache.commons.annotation;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * The class will load <code>Annotation</code> by given class
 * 
 * @author ygh 2017年1月21日
 */
public class AnnotationLoader {

    /**
     * Load <code>Annotation</code> and return a <code>Map</code> to map annotation value to
     * property name in domain class. The <code>Annotation</code> setting in property is
     * <code>Column</code>,the value of <code>ID</code> will be set in the Map
     * 
     * @param clazz The class of domain
     * @return The <code>Map</code> map <code>Column</code> value to property name in domain class
     *         or a Map' size is 0
     * @throws SQLException If class's field is null or properties is no
     */
    public static Map<String, String> ColumnMapProperty(Class<? extends Object> clazz) throws SQLException {
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            throw new SQLException(clazz.getName() + "no properties");
        }
        Map<String, String> columnToProperty = new HashMap<String, String>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Column anno = field.getAnnotation(Column.class);
            if (anno == null) {
                continue;
            }
            columnToProperty.put(anno.value(), field.getName());
        }
        return columnToProperty;
    }
    
    

    /**
     * Get <code>Annotation</code> value setting in <code>Class</code>
     * 
     * @param clazz The <code>Class</code> to provide <code>Annotation</code>
     * @return The value of <code>Annotation</code> setting in <code>Class</code>
     * @throws SQLException If the <code>Annotation</code> is null
     */
    public static String getTableNames(Class<? extends Object> clazz) throws SQLException {
        String tableName = clazz.getAnnotation(Table.class).value();
        if (tableName == null) {
            throw new SQLException("must give database table name");
        }
        return tableName;
    }

    /**
     * Get <ID> annotation value in domain class.
     * 
     * @param clazz The Class to provide <ID>
     * @return The <code>Map</code> map <code>ID</code> value to property name in domain class or a
     *         Map' size is 0
     * @throws SQLException
     */
    public static Map<String, String> getPriamryKey(Class<? extends Object> clazz) throws SQLException {
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            throw new SQLException(clazz.getName() + "no properties");
        }
        Map<String, String> columnToProperty = new HashMap<String, String>();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ID anno = field.getAnnotation(ID.class);
            if (anno == null) {
                continue;
            }
            columnToProperty.put(anno.value(), field.getName());
        }
        return columnToProperty;
    }

}
