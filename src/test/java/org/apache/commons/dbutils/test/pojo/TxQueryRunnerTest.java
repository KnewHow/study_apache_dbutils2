package org.apache.commons.dbutils.test.pojo;

import static org.junit.Assert.*;

import java.security.interfaces.RSAKey;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.JdbcUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.TxQueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.dbutils.test.pojo.TestPojo;
import org.junit.Test;

import com.mysql.fabric.xmlrpc.base.Data;

public class TxQueryRunnerTest {
    QueryRunner qr = new TxQueryRunner();

    
    @Test
    public void testBatchStringObjectArrayArray() {
      
    }

    @Test
    public void testQueryStringResultSetHandlerOfTObjectArray(){
       try {
        JdbcUtils.beginTransaction();
        fun1();
        int i = 1/0;//test commit and rollback
        JdbcUtils.commitTransaction();
    } catch (Exception e) {
        try {
            JdbcUtils.rollback();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
       
    }
    
    public void fun1() throws SQLException{
        String sql ="insert into test values(?,?,?,?)";
        Object[][] params = {{7,123,"2017-12",323},{8,123,"2017-12",323}};
        int result[] = qr.batch(sql, params);
        System.out.println(Arrays.toString(result));
    }

    @Test
    public void testQueryStringResultSetHandlerOfT() throws SQLException {
        Long time1 = System.currentTimeMillis();
        String sql ="select *from test";
        TestPojo pojo = qr.query(sql, new BeanHandler<TestPojo>(TestPojo.class));
        List<TestPojo> pojoList = qr.query(sql, new BeanListHandler<TestPojo>(TestPojo.class));
        Map<String, Object> map = qr.query(sql, new MapHandler());
        List<Map<String, Object>> mapList  = qr.query(sql, new MapListHandler());
        String sql2 = "select count(*) from test where t_id =?";
        Number number = (Number) qr.query(sql2, new ScalarHandler<Number>(),1);
        System.out.println(pojo);
        System.out.println(map);
        System.out.println(pojoList.toString());
        System.out.println(mapList.toString());
        System.out.println(number.toString());
        Long time2 = System.currentTimeMillis();
        System.out.println(time2-time1);
        
        
    }

    @Test
    public void testUpdateString() throws SQLException {
        String sql ="insert into test values(?,?,?,?)";
        Object[] params = {3,123,"2017-12",323};
        int result = qr.update(sql,params);
        System.out.println(result);
    }

    @Test
    public void testUpdateStringObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testUpdateStringObjectArray() {
        fail("Not yet implemented");
    }

}
