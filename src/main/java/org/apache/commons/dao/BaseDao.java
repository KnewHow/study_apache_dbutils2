package org.apache.commons.dao;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.annotation.AnnotationLoader;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.TxQueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.expression.Criteria;
import org.apache.commons.expression.SQLExpression;

/**
 * The class to implement <code>Dao</code> using <code>QueryRunner</code>
 * 
 * @author Administrator 2017年2月3日
 * @param <T>
 */
public class BaseDao<T> implements Dao<T> {

    /**
     * The <code>QueryRunner</code> to insert, delete, update and query
     */
    private QueryRunner qr = new TxQueryRunner();

    private static final String COMMON_STRING = "=?";

    private static final String COMMON_TABLE_NAME = "tb";

    /**
     * The class is type of <T>
     */
    public Class<T> clazz;

    /**
     * The default constructor to BaseDao, getting class of <code>T</code>
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BaseDao() {
        clazz = (Class) ((ParameterizedType) (this.getClass().getGenericSuperclass()))
                .getActualTypeArguments()[0];
    }

    public int insert(T bean) throws SQLException {
        if (bean == null) {
            throw new SQLException("bean could not be null");
        }

        List<Object> params = new ArrayList<Object>();
        String sql = this.getInsertSql(bean, params);
        return qr.update(sql, params.toArray());
    }

    public void updateAll(T bean) throws SQLException {
        if (bean == null) {
            throw new SQLException("bean could not be null");
        }
        List<Object> params = new ArrayList<Object>();
        String sql = this.getUpdateSQL(params, bean);
        qr.update(sql, params.toArray());

    }

    public void updateSelected(T bean) throws SQLException {
        if (bean == null) {
            throw new SQLException("bean could not be null");
        }
        List<Object> params = new ArrayList<Object>();
        String sql = this.getUpdateSQLSelected(params, bean);
        qr.update(sql, params.toArray());
    }

    public int delete(T bean) throws SQLException {
        List<Object> params = new ArrayList<Object>();
        String sql = getDeleteSql(params, bean);
        return qr.update(sql, params.toArray());
    }

    public T query(T bean) throws SQLException {
        if (bean == null) {
            throw new SQLException("bean could not be null");
        }
        List<Object> params = new ArrayList<Object>();
        String sql = this.getQuerySql(params, bean);
        System.out.println(sql);
        System.out.println(params.toString());
        return qr.query(sql, new BeanHandler<T>(clazz), params.toArray());
    }

    public List<T> queryList(T bean) throws SQLException {
        if (bean == null) {
            throw new SQLException("bean could not be null");
        }
        List<Object> params = new ArrayList<Object>();
        String sql = this.getQueryListSql(params, bean);
        System.out.println(sql);
        System.out.println(Arrays.toString(params.toArray()));
        return qr.query(sql, new BeanListHandler<T>(clazz), params.toArray());
    }

    public List<T> queryByCriteria(Criteria criteria) throws SQLException {
        List<SQLExpression> exprList = criteria.getExprList();
        String sql = getQueryByCriteriaSql(exprList);
        List<Object> params = this.getParams(exprList);
        System.out.println(sql);
        System.out.println(params.toString());
        return qr.query(sql, new BeanListHandler<T>(clazz), params.toArray());
    }

    /**
     * Get <code>PropertyDescriptor[]</code> by JavaBean
     * 
     * @param bean The JavaBean to provide <code>PropertyDescriptor[]</code>
     * @return The <code>PropertyDescriptor[]</code> get from JavaBeab
     * @throws SQLException If get PropertyDescriptors fail
     */
    private PropertyDescriptor[] getParameterDescriptors(T bean) throws SQLException {
        PropertyDescriptor[] props = null;
        try {
            props = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new SQLException("get PropertyDescriptors fail");
        }
        return props;
    }

    /**
     * Get insert SQL by JavaBeab
     * 
     * @param bean The JavaBean to provide data
     * @param params The parameters match "?"
     * @return The INSERT SQL
     * @throws SQLException If <code>props[i].getReadMethod().invoke(bean)</code> fail
     */
    private String getInsertSql(T bean, List<Object> params) throws SQLException {
        StringBuffer sqlFore = new StringBuffer("insert into ");
        StringBuffer sqlLast = new StringBuffer("values(");
        sqlFore.append(AnnotationLoader.getTableNames(clazz));
        sqlFore.append(" ").append("(");
        PropertyDescriptor[] props = this.getParameterDescriptors(bean);
        Map<String, String> columnToProps = AnnotationLoader.ColumnMapProperty(clazz);
        Map<String, String> primaryKeys = AnnotationLoader.getPriamryKey(clazz);
        columnToProps.putAll(primaryKeys);
        int mapSize = columnToProps.size();
        int count = 0;
        for (Map.Entry<String, String> entry : columnToProps.entrySet()) {
            sqlFore.append(entry.getKey());
            sqlLast.append("?");
            if (count < mapSize - 1) {
                sqlFore.append(",");
                sqlLast.append(",");
            }
            for (int i = 0; i < props.length; i++) {
                if (props[i].getDisplayName().equals(entry.getValue())) {
                    try {
                        params.add(props[i].getReadMethod().invoke(bean));
                    } catch (Exception e) {
                        throw new SQLException("Can't invoke Method: " + props[i].getReadMethod().getName());
                    }
                }
            }
            count++;
        }
        sqlFore.append(")");
        sqlLast.append(")");
        String sql = sqlFore.append(" ").append(sqlLast).toString();
        return sql;
    }

    /**
     * Get UPDATE SQL by given JaveBean, all value in JavaBean will be SQL whatever it is null
     * 
     * @param params The parameters match "?"
     * @param bean The JavaBean to provide data
     * @return The UPDATE SQL
     * @throws SQLException If common value is null or primary key value is null
     */
    private String getUpdateSQL(List<Object> params, T bean) throws SQLException {
        StringBuffer sqlFore = new StringBuffer("update ");
        StringBuffer sqlWhere = new StringBuffer("where ");

        sqlFore.append(AnnotationLoader.getTableNames(clazz)).append(" ");
        sqlFore.append("set ");
        Map<String, String> columnToProps = AnnotationLoader.ColumnMapProperty(clazz);
        Map<String, String> primaryKeys = AnnotationLoader.getPriamryKey(clazz);
        List<SQLExpression> exprForeList = this.getColumnSQLExpressions(columnToProps, COMMON_STRING, bean);
        List<SQLExpression> exprWhereList = this.getColumnSQLExpressions(primaryKeys, COMMON_STRING, bean);
        this.getSql(exprForeList, sqlFore, ",");
        this.getSql(exprWhereList, sqlWhere, " and ");
        String sql = sqlFore.append(" ").append(sqlWhere).toString();
        exprForeList.addAll(exprWhereList);
        params.addAll(this.getParams(exprForeList));
        return sql;
    }

    /**
     * Get UPDATE SQL by given JaveBean, only not null value will be set in SQL
     * 
     * @param params The parameters match "?"
     * @param bean The JavaBean to provide data
     * @return The UPDATE SQL
     * @throws SQLException If common value is null or primary key value is null
     */
    private String getUpdateSQLSelected(List<Object> params, T bean) throws SQLException {
        StringBuffer sqlFore = new StringBuffer("update ");
        StringBuffer sqlWhere = new StringBuffer("where ");

        sqlFore.append(AnnotationLoader.getTableNames(clazz)).append(" ");
        sqlFore.append("set ");
        Map<String, String> columnToProps = AnnotationLoader.ColumnMapProperty(clazz);
        Map<String, String> primaryKeys = AnnotationLoader.getPriamryKey(clazz);
        List<SQLExpression> exprForeList = this.getSelectedColumnSQLExpressions(columnToProps, COMMON_STRING,
                bean);
        List<SQLExpression> exprWhereList = this.getSelectedColumnSQLExpressions(primaryKeys, COMMON_STRING,
                bean);
        if (exprForeList.size() == 0 || exprWhereList.size() == 0) {
            throw new SQLException("No values set in SET or WHERE");
        }
        this.getSql(exprForeList, sqlFore, ",");
        this.getSql(exprWhereList, sqlWhere, " and ");
        String sql = sqlFore.append(" ").append(sqlWhere).toString();
        exprForeList.addAll(exprWhereList);
        params.addAll(this.getParams(exprForeList));
        return sql;
    }

    /**
     * Get query SQL
     * 
     * @param params The parameters match "?"
     * @param bean The JavaBean to provide data
     * @return The QUERY SQL
     * @throws SQLException If primary key value is null
     */
    private String getQuerySql(List<Object> params, T bean) throws SQLException {
        StringBuffer sqlWhere = new StringBuffer(" where").append(" ");
        StringBuffer sqlFore = new StringBuffer("select").append(" ");
        Map<String, String> columnToProps = AnnotationLoader.ColumnMapProperty(clazz);
        Map<String, String> primaryKeys = AnnotationLoader.getPriamryKey(clazz);
        if (primaryKeys.isEmpty()) {
            throw new SQLException("primary key could't be null");
        }
        columnToProps.putAll(primaryKeys);
        getQueryForeSql(columnToProps, sqlFore);
        List<SQLExpression> exprWhereList = this.getSelectedColumnSQLExpressions(primaryKeys, COMMON_STRING,
                bean);
        this.getSql(exprWhereList, sqlWhere, " and ");
        params.addAll(this.getParams(exprWhereList));
        return sqlFore.append(" ").append(sqlWhere).toString();
    }

    /**
     * Get QueryList SQL
     * 
     * @param params The parameters match "?"
     * @param bean The JavaBean to provide data
     * @return The QUERY SQL
     * @throws SQLException If Annotation loader fail
     */
    private String getQueryListSql(List<Object> params, T bean) throws SQLException {
        StringBuffer sqlWhere = new StringBuffer(" where").append(" ");
        StringBuffer sqlFore = new StringBuffer("select").append(" ");
        Map<String, String> columnToProps = AnnotationLoader.ColumnMapProperty(clazz);
        Map<String, String> primaryKeys = AnnotationLoader.getPriamryKey(clazz);
        columnToProps.putAll(primaryKeys);
        getQueryForeSql(columnToProps, sqlFore);
        List<SQLExpression> exprWhereList = this.getSelectedColumnSQLExpressions(columnToProps,
                COMMON_STRING, bean);
        this.getSql(exprWhereList, sqlWhere, " and ");
        params.addAll(this.getParams(exprWhereList));
        return sqlFore.append(" ").append(sqlWhere).toString();
    }

    /**
     * Get delete SQL
     * 
     * @param params The parameters match "?"
     * @param bean The JavaBean to provide data
     * @return The DELETE SQL
     * @throws SQLException If Annotation loader fail
     */
    private String getDeleteSql(List<Object> params, T bean) throws SQLException {
        StringBuffer sqlWhere = new StringBuffer(" where").append(" ");
        StringBuffer sqlFore = new StringBuffer("delete from ").append(" ")
                .append(AnnotationLoader.getTableNames(clazz)).append(" ");
        Map<String, String> columnToProps = AnnotationLoader.ColumnMapProperty(clazz);
        Map<String, String> primaryKeys = AnnotationLoader.getPriamryKey(clazz);
        columnToProps.putAll(primaryKeys);
        List<SQLExpression> exprWhereList = this.getSelectedColumnSQLExpressions(columnToProps,
                COMMON_STRING, bean);
        this.getSql(exprWhereList, sqlWhere, " and ");
        params.addAll(this.getParams(exprWhereList));
        return sqlFore.append(" ").append(sqlWhere).toString();
    }

    /**
     * Get criteria SQL
     * 
     * @param exprList The <code>List</code> of SQLExpression to provide data constructor SQL
     * @return The CRITERIA SQL
     * @throws SQLException If Annotation loader fail
     */
    private String getQueryByCriteriaSql(List<SQLExpression> exprList) throws SQLException {
        StringBuffer sqlWhere = new StringBuffer(" where").append(" ");
        StringBuffer sqlFore = new StringBuffer("select").append(" ");
        Map<String, String> columnToProps = AnnotationLoader.ColumnMapProperty(clazz);
        Map<String, String> primaryKeys = AnnotationLoader.getPriamryKey(clazz);
        columnToProps.putAll(primaryKeys);
        getQueryForeSql(columnToProps, sqlFore);
        getSql(exprList, sqlWhere, "and");
        return sqlFore.append(" ").append(sqlWhere).toString();
    }

    /**
     * Get query fore SQL, example: select tb.`uid` u_id,tb.`sex` sex,tb.`u_name` uname from s_user
     * tb
     * 
     * @param map The <code>Map</code> store all property name map to column name
     * @param sqlFore The fore SQL of query SQL, like "select "
     * @throws SQLException If Annotation loader fail
     */
    private void getQueryForeSql(Map<String, String> map, StringBuffer sqlFore) throws SQLException {
        int count = 0;
        int mapSize = map.size();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sqlFore.append(COMMON_TABLE_NAME).append(".").append("`").append(entry.getKey()).append("`")
                    .append(" ").append(entry.getValue());
            if (count < mapSize - 1) {
                sqlFore.append(",");
            }
            count++;
        }
        sqlFore.append(" from ");
        sqlFore.append(AnnotationLoader.getTableNames(clazz)).append(" ");
        sqlFore.append(COMMON_TABLE_NAME).append(" ");
    }

    /**
     * Get SQL by <code>List</code> of SQLExpression
     * 
     * @param exprList The code>List</code> of SQLExpression to provide SQL construct data
     * @param sb The fore SQL need to add
     * @param separator The separator of every variable, example: "where name=? and sex=?" the and
     *        is separator
     */
    private void getSql(List<SQLExpression> exprList, StringBuffer sb, String separator) {
        for (int i = 0; i < exprList.size(); i++) {
            SQLExpression expr = exprList.get(i);
            sb.append(expr.getName()).append(" ").append(expr.getOperator()).append(" ");
            if (i < exprList.size() - 1) {
                sb.append(separator).append(" ");
            }
        }
    }

    /**
     * Get parameter that match "?" from <code>List</code> of SQLExpression
     * 
     * @param exprList The <code>List</code> of SQLExpression to provide parameters data
     * @return The parameter matching "?" will be set <code>List</code> of Object
     */
    private List<Object> getParams(List<SQLExpression> exprList) {
        List<Object> params = new ArrayList<Object>();
        for (SQLExpression expr : exprList) {
            if (!expr.getOperator().equalsIgnoreCase("is null")) {
                params.add(expr.getValue());
            }
        }
        return params;
    }

    /**
     * Get <code>List</code> of SQLExpression to constructor SQL, all value in the JavaBean will add
     * it include null
     * 
     * @param map The <code>Map</code> store property name map column name in database table
     * @param separator The character to connect name and value Examples: select *from table where
     *        name =? and gender like and id is null the =? like is null is operator
     * @param bean The JavaBean to provide PropertyDescriptor[] and execute readMethod
     * @return
     * @throws SQLException If <code>props[i].getReadMethod().invoke(bean)</code> execute fail
     */
    private List<SQLExpression> getColumnSQLExpressions(Map<String, String> map, String separator, T bean)
            throws SQLException {
        PropertyDescriptor[] props = this.getParameterDescriptors(bean);
        List<SQLExpression> exprList = new ArrayList<SQLExpression>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            SQLExpression expr = new SQLExpression();
            expr.setName(entry.getKey());
            expr.setOperator(separator);
            for (int i = 0; i < props.length; i++) {
                if (props[i].getDisplayName().equals(entry.getValue())) {
                    try {
                        expr.setValue(props[i].getReadMethod().invoke(bean));
                    } catch (Exception e) {
                        throw new SQLException("Can't invoke Method: " + props[i].getReadMethod().getName());
                    }
                }
            }

            exprList.add(expr);
        }

        return exprList;
    }

    /**
     * Get <code>List</code> of SQLExpression to constructor SQL, only not null value will add it
     * 
     * @param map The <code>Map</code> store property name map column name in database table
     * @param separator The character to connect name and value Examples: select *from table where
     *        name =? and gender like and id is null the =? like is null is operator
     * @param bean The JavaBean to provide PropertyDescriptor[] and execute readMethod
     * @return
     * @throws SQLException If <code>props[i].getReadMethod().invoke(bean)</code> execute fail
     */
    private List<SQLExpression> getSelectedColumnSQLExpressions(Map<String, String> map, String separator,
            T bean) throws SQLException {
        PropertyDescriptor[] props = this.getParameterDescriptors(bean);
        List<SQLExpression> exprList = new ArrayList<SQLExpression>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            SQLExpression expr = new SQLExpression();
            for (int i = 0; i < props.length; i++) {
                if (props[i].getDisplayName().equals(entry.getValue())) {
                    try {
                        Object obj = props[i].getReadMethod().invoke(bean);
                        if (obj == null) {
                            break;
                        } else {
                            expr.setName(entry.getKey());
                            expr.setOperator(separator);
                            expr.setValue(obj);
                            exprList.add(expr);
                        }
                    } catch (Exception e) {
                        throw new SQLException("Can't invoke Method: " + props[i].getReadMethod().getName());
                    }
                }
            }

        }
        return exprList;
    }

}
