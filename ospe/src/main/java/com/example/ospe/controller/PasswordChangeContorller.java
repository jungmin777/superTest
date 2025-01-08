package com.example.ospe.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ospe.reservation.dto.Headerlogin;
import com.example.ospe.user.dao.DoctorRepository;
import com.example.ospe.user.dao.UserRepository;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.PasswordChangeRequest;
import com.example.ospe.user.dto.User;
import com.example.ospe.user.service.DoctorService;
import com.example.ospe.user.service.UserService;

@RestController
@RequestMapping("/api/user")
public class PasswordChangeContorller {
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private DoctorService doctorService;
	
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
	Headerlogin keep; // 로그인 유지 재사용 Headerlogin 클래스

	@ModelAttribute //모든 매핑에 추가할 코드
    public void addAttributes(Model model, Principal principal) {
        keep.headerlogin(model, principal); //로그인 유지 
    }
    
    // 비밀번호 변경
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            if (isDoctor(request.getUsername())) {
                // 의사인 경우 DoctorService 사용
                doctorService.changePassword(request.getUsername(), request.getNewPassword());
                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            } else {
                // 일반 사용자인 경우 UserService 사용
                userService.changePassword(request.getUsername(), request.getNewPassword());
                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 실패: " + e.getMessage());
        }
    }

    // roles을 기준으로 사용자가 의사인지 일반 사용자 확인
    private boolean isDoctor(String username) {
        // UserRepository에서 username으로 사용자 검색
        List<User> users = userRepository.findByUsername(username);
        if (!users.isEmpty()) {
            // User가 있으면, roles가 ADMIN일 경우 의사로 간주
            return "ADMIN".equals(users.get(0).getRoles());
        }

        // DoctorRepository에서 username으로 의사 검색
        List<Doctor> doctors = doctorRepository.findByUsername(username);
        return !doctors.isEmpty(); // 의사가 있으면 true 반환
    }
}


	
	


