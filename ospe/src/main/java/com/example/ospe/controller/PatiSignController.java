package com.example.ospe.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ospe.reservation.dto.Headerlogin;
import com.example.ospe.user.dto.User;
import com.example.ospe.user.service.UserService;

@Controller
public class PatiSignController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
	Headerlogin keep; // 로그인 유지 재사용 Headerlogin 클래스

	@ModelAttribute //모든 매핑에 추가할 코드
    public void addAttributes(Model model, Principal principal) {
        keep.headerlogin(model, principal); //로그인 유지 
    }
    
    //환자회원가입
  	@GetMapping("/patiJoin")
  	public String patiJoin() {
  		return "user/patiJoin"; 
  	}
    
    //환자 회원가입 처리
    @PostMapping("/patisign")
    public String signup(@RequestParam("username") String username, //아이디
                         @RequestParam("password") String password, //비밀번호
                         @RequestParam("name") String name, //이름
                         @RequestParam("birthDate") String birthDate, //생일
                         @RequestParam("height") Integer height, //키
                         @RequestParam("weight") Integer weight, //몸무게
                         @RequestParam("diseases") String diseases, //가지고있는 병명
                         @RequestParam("allergies") String allergies, //알레르기
                         @RequestParam("medications") String medications, //복용중인 약
                         @RequestParam("email") String email, //이메일
                         @RequestParam("emailDomain") String emailDomain, //이메일
                         @RequestParam("gender") String gender, //성별
                         @RequestParam("bloodType") String bloodType) { //혈액형
    	

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);
       
        // 최종 이메일 처리
        String finalEmail = emailDomain.equals("custom") ? email : email + "@" + emailDomain;

        // 새로운 User 객체 생성 후 데이터베이스에 저장
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setName(name);
        user.setBirthDate(LocalDate.parse(birthDate));  // String을 LocalDate로 변환
        user.setHeight(height);
        user.setWeight(weight);
        user.setDiseases(diseases);
        user.setAllergies(allergies);
        user.setMedications(medications);
        user.setEmail(finalEmail);
        user.setGender(gender);
        user.setBloodType(bloodType);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUseYn("Y");

        userService.saveUser(user);  // UserService의 saveUser 호출
        return "login";  // 회원가입 후 로그인 페이지로 리다이렉트
    }
    
    //아이디 중복확인
    @GetMapping
    @RequestMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameTaken(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    

}

