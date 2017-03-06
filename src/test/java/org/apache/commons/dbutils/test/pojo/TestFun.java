package org.apache.commons.dbutils.test.pojo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.JdbcUtils;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.test.pojo.TestPojo;
import org.junit.Test;

/**
 * 
 * @author ygh 2016年12月27日
 */
public class TestFun {

    /**
     * Provide to test
     * 
     * @return A ResultSet that its next() hasn't call
     * @throws SQLException If a database access error occurs
     */
    public ResultSet getResultSet() throws SQLException {
        Connection con = JdbcUtils.getConnection();
        String sql = "select *from test";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.execute();
        ResultSet rs = preparedStatement.getResultSet();
        return rs;
    }
    
    @Test
    public void getResultSet2() throws SQLException {
        Connection con = JdbcUtils.getConnection();
        String sql = "select count(*) from test where t_id =1";
        PreparedStatement preparedStatement = con.prepareStatement(sql);
        preparedStatement.execute();
        ResultSet rs = preparedStatement.getResultSet();
        if(rs.next()){
            System.out.println(rs.getObject(1));
        }
    }

    /**
     * Test new BeanProcessor().toBean(rs, TestPojo.class);
     * 
     * @throws SQLException If a database access or occurs.
     */
    @Test
    public void fun1_1() throws SQLException {
        ResultSet rs = this.getResultSet();
        if (rs.next()) {
            TestPojo test = new BeanProcessor().toBean(rs, TestPojo.class);
            System.out.println(test.toString());
        }
    }

    /**
     * Test new BeanProcessor().toBeanList(rs, TestPojo.class);
     * 
     * @throws SQLException If a database access error occurs
     */
    @Test
    public void fun1_2() throws SQLException {
        ResultSet rs = this.getResultSet();
        List<TestPojo> pojoList = new BeanProcessor().toBeanList(rs, TestPojo.class);
        System.out.println(pojoList.toString());
    }

    /**
     * Test new BasicRowProcessor().toBean(rs, TestPojo.class);
     * 
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void fun1_3() throws SQLException {
        ResultSet rs = this.getResultSet();
        if (rs.next()) {
            TestPojo test = new BasicRowProcessor().toBean(rs, TestPojo.class);
            System.out.println(test.toString());
        }
    }

    /**
     * Test new BasicRowProcessor().toBeanList(rs, TestPojo.class);
     * 
     * @throws SQLException If a database access error occurs
     */
    @Test
    public void fun1_4() throws SQLException {
        ResultSet rs = this.getResultSet();
        List<TestPojo> pojoList = new BasicRowProcessor().toBeanList(rs, TestPojo.class);
        System.out.println(pojoList.toString());
    }

    /**
     * Test new BasicRowProcessor().toArray(rs);
     * @throws SQLException If a database access error occurs.
     */
    @Test
    public void fun1_5() throws SQLException {
        ResultSet rs = this.getResultSet();
        if(rs.next()){
            Object[] pojoArr = new BasicRowProcessor().toArray(rs);
            System.out.println(Arrays.toString(pojoArr));
        }
    }
    
    /**
     * Test BasicRowProcessor().toMap(rs);
     * @throws SQLException If a database access error occurs
     */
    @Test
    public void fun1_6() throws SQLException {
        ResultSet rs = this.getResultSet();
        if(rs.next()){
            Map<String, Object> pojoMap = new BasicRowProcessor().toMap(rs);
            System.out.println(pojoMap);
        }
    }

}