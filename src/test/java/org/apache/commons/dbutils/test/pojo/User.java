package org.apache.commons.dbutils.test.pojo;

import org.apache.commons.annotation.Column;
import org.apache.commons.annotation.ID;
import org.apache.commons.annotation.Table;

@Table("s_user")
public class User {

    public User() {
    }

    public User(Integer u_id, String uname, String sex) {
        this.u_id = u_id;
        this.uname = uname;
        this.sex = sex;
    }

    @ID("uid")
    private Integer u_id;

    @Column("u_name")
    private String uname;

    @Column("sex")
    private String sex;

    public Integer getU_id() {
        return u_id;
    }

    public void setU_id(Integer u_id) {
        this.u_id = u_id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User [u_id=" + u_id + ", uname=" + uname + ", sex=" + sex + "]";
    }

}
