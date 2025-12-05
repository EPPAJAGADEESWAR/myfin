package com.myfin.admin.service;

import com.myfin.admin.entity.Admin;
import com.myfin.admin.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

//Service layer for admin operations and approvals
@Service
public class AdminService {

	private final AdminRepository adminRepository;

	public AdminService(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}

	public Admin register(Admin admin) {
		admin.setStatus("ACTIVE");
		admin.setRole("ADMIN");
		return adminRepository.save(admin);
	}

	public Optional<Admin> login(String email, String password) {
		return adminRepository.findByEmail(email).filter(a -> a.getPassword().equals(password));
	}

}
