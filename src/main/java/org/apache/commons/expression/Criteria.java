package org.apache.commons.expression;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is to store query criteria
 * 
 * @author ygh 2017年1月23日
 */
public class Criteria {

    /**
     * The <code>List</code> store the query criteria
     */
    private List<SQLExpression> exprList;

    /**
     * The default constructor of Criteria
     */
    public Criteria() {
        this.exprList = new ArrayList<SQLExpression>();
    }

    /**
     * Store criteria like "where uname =?"
     * 
     * @param criteriaName The name of variable in database table
     * @param criteriaValue The value match ?
     */
    public void equalColumn(String criteriaName, Object criteriaValue) {
        SQLExpression expr = new SQLExpression();
        expr.setName(criteriaName);
        expr.setOperator("=?");
        expr.setValue(criteriaValue);
        this.exprList.add(expr);
    }

    /**
     * Store criteria like "where uname like ?"
     * 
     * @param criteriaName The name of variable in database table
     * @param criteriaValue The value match ?
     */
    public void likeColumn(String criteriaName, Object criteriaValue) {
        SQLExpression expr = new SQLExpression();
        expr.setName(criteriaName);
        expr.setOperator("like ?");
        expr.setValue("%" + criteriaValue + "%");
        this.exprList.add(expr);
    }

    /**
     * Store criteria like "where uname is null"
     * 
     * @param criteriaName The name of variable in database table
     */
    public void isNullColumn(String criteriaName) {
        SQLExpression expr = new SQLExpression();
        expr.setName(criteriaName);
        expr.setOperator("is null");
        expr.setValue(null);
        this.exprList.add(expr);
    }

    public List<SQLExpression> getExprList() {
        return exprList;
    }

}
