package com.sujan.bean;

import javax.persistence.*;

/**
 * @author Sujan Maharjan
 * @since 02-25-2019
 */

@Entity
@Table(name = "tbl_admin")
public class Admin {

    //	@GeneratedValue(strategy=GenerationType.TABLE)

    @Id
    @Column(name = "admin_name", updatable = false, nullable = false)
    private String adminName;

    private String password;

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin [Name=" + adminName + "]";
    }


}
