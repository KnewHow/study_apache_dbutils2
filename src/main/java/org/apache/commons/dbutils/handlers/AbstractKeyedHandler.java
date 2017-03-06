package org.apache.commons.dbutils.handlers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbutils.ResultSetHandler;
/**
 * <p>
 *      <code>ResultSetHandler</code> implementation that returns a Map.
 *      <code>ResultSet</code> can be converted into objects(Vs) which are then stored in a Map
 *      under given keys(Ks).
 *      
 * </p>
 * @author ygh
 * 2017年1月7日
 * @param <K> the type of keys maintained by the returned map
 * @param <V> the type of mapped values
 */
public abstract class AbstractKeyedHandler<K, V> implements ResultSetHandler<Map<K, V>> {

    /**Convert each row's columns into a Map and store then in a <code>Map</code> under <code>ResultSet.getObject(key)</code>
     * @param rs <code>ResultSet</code>  to process
     * @return A <code>Map</code> never <code>null</code>.
     * @throws SQLException If a database access error occurs. 
     * 
     */
    public Map<K, V> handle(ResultSet rs) throws SQLException {
        Map<K, V> result = creatMap();
        while(rs.next()){
            result.put(createKey(rs), creatRow(rs));
        }
        return result;
    }

    /**
     * This is factory method is called by <code>handle</code> to create the Map to stored in. This
     * implementation return a <code>HashMap</code> instance
     * 
     * @return A Map to store records in.
     */
    protected Map<K, V> creatMap() {
        return new HashMap<K, V>();
    }
    
    
    /**
     * The factory method is called by <code>handle</code> to retrieve the key value
     * from the current <code>ResultSet</code> row
     * @param rs The <code>ResultSet</code> to create key from
     * @return K from the configured key column name/index
     * @throws SQLException If a database access error occurs
     */
    protected abstract K createKey(ResultSet rs) throws SQLException;
   
    
    
    /**
     * This factory method is called by <code>handle</code> to store the current
     * <code>ResultSet</code> row in some java objects
     * @param rs The <code>ResultSet</code> to create row from
     * @return V object create from the current row
     * @throws SQLException If a database access error occurs
     */
    protected abstract V creatRow(ResultSet rs) throws SQLException;

}
