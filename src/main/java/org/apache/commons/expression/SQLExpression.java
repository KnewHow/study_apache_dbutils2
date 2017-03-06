package org.apache.commons.expression;

/**
 * This class store criteria to constructor SQL
 * @author ygh
 * 2017年1月22日
 */
public class SQLExpression {

    /**
     * The name of variable in database table
     */
    private String name;//变量的名称
    
    /**
     * The character to connect name and value
     * Examples: select *from table where name =? and gender like and id is null
     * the =? like is null is operator
     */
    private String operator;//变量符号
    
    /**
     * The value of the ? or others
     */
    private Object value;//变量的值

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
    
}
