package com.sujan.service.impl;

import com.sujan.bean.Admin;
import com.sujan.repository.AdminRepository;
import com.sujan.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public boolean authenticate(String adminName, String password) {
        Admin admin = this.findByAdminName(adminName);
        if (admin == null) {
            return false;
        } else {
            return password.equals(admin.getPassword());
        }
    }

    @Override
    public Admin findByAdminName(String adminName) {
        return adminRepository.findByAdminName(adminName);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public Admin update(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public void delete(Admin admin) {
        adminRepository.delete(admin);
    }

    @Override
    public void delete(Long id) {
    }

    @Override
    public void deleteInBatch(List<Admin> admins) {
        adminRepository.deleteInBatch(admins);
    }

    @Override
    public Admin find(Long id) {
        return null;
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }
}
