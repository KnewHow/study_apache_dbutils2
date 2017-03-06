package org.apache.commons.dbutils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Basic implementation of <code>RowProcessor</code> interface
 * <p>
 * The class is thread safe.
 * </P>
 * 
 * @author ygh 2017年1月6日
 */
public class BasicRowProcessor implements RowProcessor {
    /**
     * The default BeanProcessor to use if not supplied in constructor.
     */
    private static final BeanProcessor defaultConvert = new BeanProcessor();

    /**
     * The singleton instance of this class
     */
    private static final BasicRowProcessor instance = new BasicRowProcessor();

    /**
     * Return this singleton instance of this class.
     * 
     * @return The singleton instance of this class
     */
    public static BasicRowProcessor instance() {
        return instance;
    }

    /**
     * Use this to process beans.
     */
    private final BeanProcessor convert;

    /**
     * BasicRowProcessor constructor. Bean processing is default processor(
     * <code>defaultConvert</code>)
     */
    public BasicRowProcessor() {
        this(defaultConvert);
    }

    public BasicRowProcessor(BeanProcessor convert) {
        this.convert = convert;
    }

    /**
     * Convert a<code>ResultSet</code> into an <code>Object[]</code>. This implementation copies
     * column value into the array in same order they're returned from <code>ResultSet</code> Array
     * elements will be set <code>null</code> if the column was SQL NULL.
     * 
     * @param rs The ResultSet that supplies data
     * @throws SQLException If a database access error occurs.
     * @return the newly created array
     */
    public Object[] toArray(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        Object[] result = new Object[cols];
        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i+1);
        }
        return result;
    }

    /**
     * Convert a <code>ResultSet</code> into a JavaBean. This implementation delegates to a
     * BeanProcessor instance
     * 
     * @param <T> The type of bean to create
     * @param rs The ResultSet that supplies data
     * @param type The type of created bean
     * @return the newly JavaBean
     * @throws SQLException If a database access error occurs
     */
    public <T> T toBean(ResultSet rs, Class<? extends T> type) throws SQLException {

        return this.convert.toBean(rs, type);
    }

    /**
     * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBean. This implementation
     * delegate to a BeanProcessor instance.
     * 
     * @param <T> The type of bean to create
     * @param rs The ResultSet that supplies bean data
     * @param type The type of bean to create
     * @return An newly List of JavaBean
     * @throws If a database access error occurs
     */
    public <T> List<T> toBeanList(ResultSet rs, Class<? extends T> type) throws SQLException {
        return this.convert.toBeanList(rs, type);
    }

    /**
     * Convert <code>ResultSet</code> row into a <code>Map</code>
     * 
     * <p>
     * This implementation return a <code>Map</code> with case insensitive column names as keys. Call to
     * <code>map.get("COL")</code> and <code>map.get("col")</code> return same value. Furthermore this implementation
     * will return an order map that preserves the ordering of columns in the ResulSet, so that iterating over 
     * the entry set of the returned map will return the first column of ResultSet, then the second and so forth.
     * </p>
     * 
     * @param rs The ResultSet supplies the map data
     * @return the newly created map
     * @throws SQLException If a database access error occurs
     */
    public Map<String, Object> toMap(ResultSet rs) throws SQLException {
        Map<String, Object> result = new CaseInsensitiveHashMap();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        for(int i=1;i<=cols;i++){
            String columnName = rsmd.getColumnLabel(i);
            if(columnName==null||columnName.length()==0){
                columnName = rsmd.getColumnName(i);
            }
            result.put(columnName, rs.getObject(i));
        }
        return result;
    }

    /**
     * A Map convert all key to lowercase Strings for case insensitive lookups This is needed for
     * the toMap() implementation because database don't consistently handle the casing of column
     * 
     * <p>
     * The keys are stored as they are give [BUG #DBUTILS-34], so we maintain an internal mapping
     * from lowercase keys to the real keys in order to achieve the case insensitive lookup
     * 
     * <p>
     *  Note:This implementation does not allow <code>null</code> for key, because of the code
     *  <pre>
     *      key.toString().toLowerCase();
     *  </pre>
     *  
     * </p>
     * </p>
     * 
     * @author ygh 2017年1月6日
     */
    public static class CaseInsensitiveHashMap extends LinkedHashMap<String, Object> {
        /**
         * The internal mapping from lowercase keys to real keys
         * <p>
         * Any query operation using the key 
         * {@link #get(Object)}, {@link #containsKey(Object)} 
         * is done in three steps:
         * <ul>
         *   <li>
         *      Convert the parameter key to lower case
         *   </li>
         *   <li>
         *      Get the actual key that corresponds to lower case key
         *   </li>
         *   <li>
         *      query the map with actual key
         *   </li>
         * </ul>
         * </p>
         */
        private final Map<String, String> lowerCaseMap = new HashMap<String, String>();
        
        /**
         * Required for serialization support.
         *
         * @see java.io.Serializable
         */
        private static final long serialVersionUID = -2848100435296897392L;

        

        @Override
        public boolean containsKey(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.containsKey(realKey);
        }

        @Override
        public Object get(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.get(realKey);
        }

        @Override
        public Object put(String key, Object value) {
            Object oldKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            Object oldValue = super.remove(oldKey);
            super.put(key, value);
            return oldValue;
        }

        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
            for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
                String key = entry.getKey();
                String value = entry.getKey();
                this.put(key, value);
            }
        }

        @Override
        public Object remove(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase(Locale.ENGLISH));
            return super.remove(realKey);
        }
        
    }

}
