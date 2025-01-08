package com.example.ospe.reservation.dto;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.example.ospe.user.dao.DoctorRepository;
import com.example.ospe.user.dao.UserRepository;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;

// 로그인 유지 코드 재사용 위한 클래스
@Component
public class Headerlogin {
	
	@Autowired
	UserRepository userrep;
	@Autowired
	DoctorRepository doctorrep;
	
	public void headerlogin(Model model, Principal principal) {
		if (principal != null) {
			List<User> userlist =  userrep.findByUsername(principal.getName());
			List<Doctor> doctorlist = doctorrep.findByUsername(principal.getName());
			
			
			if(userlist.isEmpty()) {
				model.addAttribute("roles","ADMIN");
			}
			
	         // 사용자가 로그인한 경우
	         model.addAttribute("loggedIn", true);
	         model.addAttribute("username", principal.getName());
	      } else {
	         // 사용자가 로그인하지 않은 경우
	         model.addAttribute("loggedIn", false);
	      }
	}
}
