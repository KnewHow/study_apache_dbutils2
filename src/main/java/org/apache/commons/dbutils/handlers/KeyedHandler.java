package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.RowProcessor;
/**
 * <p>
 * <code>ResultSetHandeler</code> implementation that returns a Map of Map
 * <code>ResultSet</code> rows are converted into Maps which are then stored in a Map
 * under given key.
 * </p>
 * <p>
 * If you hava a Person table with a primary key called ID, you could retrieve
 * row from table like this:
 * <pre>
 * ResultSetHandler h = new KeyedHandler("id");
 * Map found = (Map) queryRunner.query("select id, name, age from person", h);
 * Map jane = (Map) found.get(new Long(1)); // jane's id is 1
 * String janesName = (String) jane.get("name");
 * Integer janesAge = (Integer) jane.get("age");
 * </pre>
 * 
 * Note that the "id" passed to KeyHandler and "name" and "age" passed to the
 * returned Map's get() method can be in any case. The data types returned for
 * name and age are dependent upon how your JDBC driver convert SQL column types from
 * the Person table into Java types
 * <p>
 * 
 * This class is thread safe
 * @author ygh
 * 2017年1月9日
 * @param <K> The type of the key
 */
public class KeyedHandler<K> extends AbstractKeyedHandler<K, Map<String, Object>>{
    
    /**
     * The <code>RowProcessor</code> implementation to use when converting 
     * a <code>ResultSet</code> into a <code>Map</code>
     */
    protected final RowProcessor convert;
    
    /**
     * The column index to retrieve from <code>ResultSet</code>
     */
    protected final int columnIndex;
    
    /**
     * The column name to retrieve from <code>ResultSet</code>
     */
    protected final String columnName;
    
    
    /**
     * Create new instance of KeyedHandler. The value of first column of
     * each will be a key in the map
     */
    public KeyedHandler() {
        this(ArrayHandler.ROW_PROCESSOR,1,null);
    }
    
    
    /**
     * Create new instance of KeyedHandler. The value of first column of
     * each will be a key in the map
     * @param convert The <code>RowProcessor</code> implementation to use
     * when converting a <code>Result</code> rows into <code>Map</code>
     */
    public KeyedHandler(RowProcessor convert) {
        this(convert,1,null);
    }
    
    

    
    /**
     * Create a new instance of KeyedHandler
     * @param columnIndex The value to used as the keys in the Map are
     * retrieve from the column at this index
     */
    public KeyedHandler(int columnIndex) {
        this(ArrayHandler.ROW_PROCESSOR,columnIndex,null);
    }

    /**
     * Create a new instance of KeyedHandler
     * @param columnName The value to used as the key in the map are
     * retrieve from the column with this name
     */
    public KeyedHandler(String columnName) {
        this(ArrayHandler.ROW_PROCESSOR,1,columnName);
    }


    /**
     * Create a new instance of KeyedHandler, the constructor is private
     * @param convert The <code>RowProcessor</code> implementation to use when converting
     * a <code>RessultSet</code> row into a <code>Map</code>
     * @param columnIndex The column index to retrieve from <code>ResultSet</code>
     * @param columnName The column name to retrieve from <code>ResultSet</code>
     */
    private KeyedHandler(RowProcessor convert, int columnIndex, String columnName) {
        this.convert = convert;
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    /**
     * This factory method is called by <code>handle()</code> to retrieve
     * the key value from the current <code>Result</code> row. 
     * This implementation returns <code>rs.getObject()</code> for
     * configured key column name or index
     * @param rs The <code>ResultSet</code> to create key from
     * @return Object from the configured key column name/index
     * @throws SQLException If a database access error occurs
     */
    @SuppressWarnings("unchecked")
    @Override
    protected K createKey(ResultSet rs) throws SQLException {
        return (this.columnName==null)?
                (K)rs.getObject(this.columnIndex)
                :(K)rs.getObject(this.columnName);
    }

    /**
     * This factory is called by <code>handler()</code> to stored the 
     * current <code>ResultSet</code> row in some object. This implementation
     * return a <code>Map</code> with case insensitive column name as keys.
     * Calls to <code>map.get("COL")</code> and <code>map.get("col")</code>
     * get the same value.
     * 
     * @param rs The <code>ResultSet </code> to create Map from
     * @return Object typed Map containing column names to value
     * @throws SQLException If a database access error occurs.
     */
    @Override
    protected Map<String, Object> creatRow(ResultSet rs) throws SQLException {
        return this.convert.toMap(rs);
    }

}
