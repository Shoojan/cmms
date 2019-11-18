package com.sujan.repository;

import com.sujan.bean.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {

	Admin findByAdminName(String adminName);
}
