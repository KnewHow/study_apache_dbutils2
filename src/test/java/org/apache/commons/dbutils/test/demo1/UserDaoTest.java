package org.apache.commons.dbutils.test.demo1;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.TxQueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.test.dao.UserDao;
import org.apache.commons.dbutils.test.pojo.User;
import org.apache.commons.expression.Criteria;
import org.junit.Test;

public class UserDaoTest {

    @Test
    public void fun1() throws SQLException {
        User user = new User(4, "是多少4", "那边4");
        new UserDao().insert(user);
    }

    /**
     * @throws SQLException
     */
    @Test
    public void fun2() throws SQLException {
        User user = new User();
        user.setU_id(1);
        new UserDao().updateAll(user);
    }

    @Test
    public void fun3() throws SQLException {
        User user = new User(3, "是多少", null);
        user.setU_id(1);
        new UserDao().updateSelected(user);
    }

    /**
     * @throws SQLException
     */
    @Test
    public void fun4() throws SQLException {
        User user = new User();
        user.setU_id(1);
        User bean = new UserDao().query(user);
        System.out.println(bean.toString());
    }

    @Test
    public void fun5() throws SQLException {
        User user = new User();
        user.setUname("ygh");
        List<User> list = new UserDao().queryList(user);
         System.out.println(list.toString());
    }
    @Test
    public void fun6() throws SQLException {
        User user = new User();
        user.setUname("是多少");
        List<User> list = new UserDao().queryList(user);
        System.out.println(list.toString());
    }
    @Test
    public void fun7() throws SQLException {
        QueryRunner qr = new TxQueryRunner();
        String sql = "select tb.`uid` u_id,tb.`sex` sex,tb.`u_name` uname from s_user tb   where u_name like ?";
        List<User> list = qr.query(sql, new BeanListHandler<User>(User.class),"%是%");
        System.out.println(list.toString());
    }
    @Test
    public void fun8() throws SQLException {
        QueryRunner qr = new TxQueryRunner();
        String sql = "SELECT tb.`uid` u_id,tb.`sex` sex,tb.`u_name` uname FROM s_user tb  WHERE u_name LIKE ?";
        List<User> list = qr.query(sql, new BeanListHandler<User>(User.class),"%是%");
        System.out.println(list.toString());
    }
    @Test
    public void fun9() throws SQLException {
        Criteria criteria = new Criteria();
        criteria.likeColumn("u_name", "y");
        List<User> list = new UserDao().queryByCriteria(criteria);
        System.out.println(list.toString());
    }
    @Test
    public void fun10() throws SQLException {
        Criteria criteria = new Criteria();
        criteria.isNullColumn("u_name");
        List<User> list = new UserDao().queryByCriteria(criteria);
        System.out.println(list.toString());
    }
    @Test
    public void fun11() throws SQLException {
        QueryRunner qr = new TxQueryRunner();
        String sql = "select tb.`uid` u_id,tb.`sex` sex,tb.`u_name` uname from s_user tb   where u_name is null";
        List<User> list = qr.query(sql, new BeanListHandler<User>(User.class));
        System.out.println(list.toString());
    }
    
    @Test
    public void fun12() throws SQLException {
        Criteria criteria = new Criteria();
        criteria.equalColumn("uid", 2);
        criteria.isNullColumn("u_name");
        criteria.likeColumn("sex", "男");
        List<User> list = new UserDao().queryByCriteria(criteria);
        System.out.println(list.toString());
    }
    @Test
    public void fun13() throws SQLException {
        User user = new User();
        user.setU_id(4);
        user.setSex("4");
        int list = new UserDao().delete(user);
        System.out.println(list);
    }
    
    
}
