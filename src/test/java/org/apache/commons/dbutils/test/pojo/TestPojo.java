package org.apache.commons.dbutils.test.pojo;

import org.apache.commons.annotation.Column;
import org.apache.commons.annotation.Table;


@Table("test")
public class TestPojo {

    @Column("t_id")
    private String t_id;

    @Column("number")
    private Integer number;

    @Column("t_time")
    private String t_time;
    
    @Column("sum")
    private int sum;

    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getT_time() {
        return t_time;
    }

    public void setT_time(String t_time) {
        this.t_time = t_time;
    }
    

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    @Override
    public String toString() {
        return "TestPojo [t_id=" + t_id + ", number=" + number + ", t_time=" + t_time + ", sum=" + sum + "]";
    }
    

}
