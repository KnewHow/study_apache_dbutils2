package org.apache.commons.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.expression.Criteria;

/**
 * The interface to provide method that implementations will implement it
 * 
 * @author Administrator 2017年2月3日
 * @param <T>
 */
public interface Dao<T> {

    /**
     * This method is to insert a JavaBean into database table
     * 
     * @param bean The Object bean needed to insert into
     * @return The number of rows affected
     * @throws SQLException If a database access error occurs
     */
    public int insert(T bean) throws SQLException;

    /**
     * This method is to update all values in the table row from given JavaBean by primary key. If
     * the value is null in JavaBean, the value will be updated into NULL in table row after execute
     * this method.
     * 
     * @param bean The JavaBean to provide data must contains a primary key value
     * @throws SQLException If a database access error occurs
     */
    public void updateAll(T bean) throws SQLException;

    /**
     * This method is to update all values in the table row from given JavaBean by primary key. If
     * the value is null in JavaBean, The value will be change in table row after execute this
     * method
     * 
     * @param bean The JavaBean to provide data must contains a primary key value
     * @throws SQLException If a database access error occurs
     */
    public void updateSelected(T bean) throws SQLException;

    /**
     * This method is to delete rows in database table by given JavaBean, The criteria is AND
     * 
     * @param bean The JavaBean to provide data from which delete criteria get
     * @return The number of rows affected
     * @throws SQLException If a database access error occurs
     */
    public int delete(T bean) throws SQLException;

    /**
     * Query data from database table by primary key set in JavaBean
     * 
     * @param bean The JavaBean that provide primary key to query
     * @return The new JavaBean created fill data from database
     * @throws SQLException If a database access error occurs
     */
    public T query(T bean) throws SQLException;

    /**
     * Query data from database table by criteria provided by JavaBean
     * 
     * @param bean The JavaBean provide data to query
     * @return The <code>List</code> of new JavaBean created fill data from database table
     * @throws SQLException If a database access error occurs
     */
    public List<T> queryList(T bean) throws SQLException;

    /**
     * Query data from database table by criteria
     * 
     * @param criteria The criteria will be set by caller
     * @return The <code>List</code> of new JavaBean created fill data from database table
     * @throws SQLException If a database access error occurs
     */
    public List<T> queryByCriteria(Criteria criteria) throws SQLException;
}
