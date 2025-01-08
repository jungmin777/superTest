package com.example.ospe.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.service.DoctorService;

@Controller
public class DoctorSignController {

	@Autowired
	private DoctorService doctorService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	//의사회원가입
	@GetMapping("/doctorJoin")
	public String doctorJoin() {
		return "user/doctorJoin"; 
	}

	// 의사 회원가입 처리
	@PostMapping("/doctorsign")
	public String signup(
			@RequestParam("username") String username, //아이디
			@RequestParam("password") String password, //비밀번호
			@RequestParam("name") String name, //이름
			@RequestParam("birthDate") String birthDate, //생일
			@RequestParam("hospital_name") String hospital_name, //병원명
			@RequestParam("hospital_address") String hospital_address, //병원주소
			@RequestParam("specialty") String specialty, //의사 주전공
			@RequestParam("emailDomain") String emailDomain, //이메일 도메인
			@RequestParam("email") String email) { //이메일

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(password);
		
        // 최종 이메일 처리
        String finalEmail = emailDomain.equals("custom") ? email : email + "@" + emailDomain;

		// 새로운 Doctor 객체 생성 후 데이터베이스에 저장
		Doctor doctor = new Doctor();
		doctor.setUsername(username);
		doctor.setPassword(encodedPassword);
		doctor.setName(name);
		doctor.setBirthDate(LocalDate.parse(birthDate)); // String을 LocalDate로 변환
        doctor.setEmail(finalEmail);

		// 병원 관련 정보 추가
		doctor.setHospitalName(hospital_name);
		doctor.setHospitalAddress(hospital_address);
		doctor.setSpecialty(specialty);

		// 생성일시 및 수정일시 설정
		doctor.setCreatedAt(LocalDateTime.now());
		doctor.setUpdatedAt(LocalDateTime.now());

		// DoctorService의 saveDoctor 호출
		doctorService.saveDoctor(doctor); // 회원가입 처리 후 의사 정보를 저장
		
		return "redirect:/login";
	}
	
	
	
    //아이디 중복확인
    @GetMapping
    @RequestMapping("doctor/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = doctorService.isUsernameTaken(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

}
