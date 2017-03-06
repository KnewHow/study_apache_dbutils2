package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.RowProcessor;

/**
 * <p>
 * <code>ResultSetHandler</code> implementation that returns a <code>Map</code> of Beans.
 * <code>ResultSet</code> rows are converted into Beans which are then store in a <code>Map</code>
 * under given key.
 * </p>
 * <p>
 * If you have Person table with a primary key column called ID, you could retrieve rows from the
 * table like this:
 * 
 * <pre>
 * ResultSetHandler&lt;Map&lt;Long, Person&gt;&gt; h = new BeanMapHandler&lt;Long, Person&gt;(Person.class, &quot;id&quot;);
 * 
 * Map&lt;Long, Person&gt; found = queryRunner.query(&quot;select id, name, age from person&quot;, h);
 * 
 * Person jane = found.get(1L); // jane's id is 1
 * 
 * String janesName = jane.getName();
 * 
 * Integer janesAge = jane.getAge();
 * </pre>
 * 
 * </p>
 * 
 * Note that the "id" passed to the BeanMapHandler can be in any case. The data type returned for id
 * is dependent upon how your JDBC driver convert SQL column types from Person table into java
 * types. The "name" and "age" columns are converted according their property descriptors by DBUtils
 * 
 * <p>
 * The class is thread safe
 * </p>
 * 
 * @author ygh 2017年1月8日
 * @param <K> The type of keys maintained by the the returned map
 * @param <V> the type of bean
 */
public class BeanMapHandler<K, V> extends AbstractKeyedHandler<K, V> {

    /**
     * The Class of bean produced by this handler
     * 
     */
    private final Class<V> type;

    /**
     * The <code>RowProcessor</code> implementation to use when converting rows into Objects
     */
    private final RowProcessor convert;

    /**
     * The column index to retrieve key values from. The default is 1.
     */
    private final int columnIndex;

    /**
     * The column name to retrieve key values from. Either columnName or columnIndex will be used
     * but never both
     */
    private final String columnName;

    /**
     * Create a new instance of BeanMapHandler. The value of first column of each row will be a key
     * of Map
     * 
     * @param type The Class that objects returned from <code>createRow()</code> are created from.
     */
    public BeanMapHandler(Class<V> type) {
        this(type, ArrayHandler.ROW_PROCESSOR, 1, null);
    }

    /**
     * Create a new instance of BeanMapHandler. The value of first column of each row will be a key
     * of Map
     * 
     * @param type The Class that objects returned from <code>createRow()</code> are created from
     * @param convert The <code>RowProcessor</code> implementation to use when converting rows into
     *        Beans.
     */
    public BeanMapHandler(Class<V> type, RowProcessor convert) {
        this(type, convert, 1, null);
    }

    /**
     * Create a new instance of BeanMapHandler.
     *
     * @param type The Class that objects returned from <code>createRow()</code> are created from
     * @param columnIndex The values to use as keys in the Map are retrieve from the column at this
     *        index.
     */
    public BeanMapHandler(Class<V> type, int columnIndex) {
        this(type, ArrayHandler.ROW_PROCESSOR, columnIndex, null);
    }

    /**
     * Create a new instance of BeanMapHandler.
     * 
     * @param type The Class that objects returned from <code>createRow</code> are create from.
     * @param columnName The values to use as keys in the Map are retrieve from column with this
     *        name
     */
    public BeanMapHandler(Class<V> type, String columnName) {
        this(type, ArrayHandler.ROW_PROCESSOR, 1, columnName);
    }

    /**
     * Private Constructor of BeanMapHandler
     * 
     * @param type The Class that objects returned from <code>CreateRow()</code> are create from
     * @param convert The <code>RowProcessor</code> implementation to use when converting whole
     *        <code>ResultSet</code> to a <code>Map</code> of JavaBeans
     * 
     * @param columnIndex The value to use as keys in Map are retrieve from the column at this index
     * @param columnName The value to use as keys in Map are retrieve from column with this name
     */
    public BeanMapHandler(Class<V> type, RowProcessor convert, int columnIndex, String columnName) {
        this.type = type;
        this.convert = convert;
        this.columnIndex = columnIndex;
        this.columnName = columnName;
    }

    /**
     * This is factory method is called by <code>handler()</code> to retrieve
     * the key value from the current <code>ResultSet</code> row
     * @param rs <code>ResultSet</code> to create a key from
     * @param K from the configured key column name/index
     * @throws SQLException If a database access error occurs.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected K createKey(ResultSet rs) throws SQLException {
        return (this.columnName==null)?
                (K)rs.getObject(this.columnIndex)
                :(K) rs.getObject(this.columnName);
    }

    
    @Override
    protected V creatRow(ResultSet rs) throws SQLException {
        return this.convert.toBean(rs, type);
    }

}
