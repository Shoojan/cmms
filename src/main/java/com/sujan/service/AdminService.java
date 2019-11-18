package com.sujan.service;

import com.sujan.bean.Admin;
import com.sujan.service.generic.GenericService;

import java.util.List;

public interface AdminService extends GenericService<Admin> {

    boolean authenticate(String adminName, String password);
    Admin findByAdminName(String adminName);
    List<Admin> getAllAdmins();

}
