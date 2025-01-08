package com.example.ospe.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ospe.reservation.dto.Headerlogin;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;
import com.example.ospe.user.service.DoctorService;
import com.example.ospe.user.service.UserService;

@Controller
public class ViewController {

	@Autowired
	private UserService userService;
	@Autowired
	private DoctorService doctorService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	Headerlogin keep; // 로그인 유지 재사용 Headerlogin 클래스

	@ModelAttribute //모든 매핑에 추가할 코드
    public void addAttributes(Model model, Principal principal) {
        keep.headerlogin(model, principal); //로그인 유지 
    }
	
	@GetMapping("/")
	public String goHome() {
		return "redirect:/login";
	}

	
	@GetMapping("/header")
	public String getHeader(Model model, Principal principal) { // principal -> 현재 인증된 사용자의 정보 담고있는 객체
		if (principal != null) {
			// 사용자가 로그인한 경우
			model.addAttribute("loggedIn", true);
			model.addAttribute("username", principal.getName());
		} else {
			// 사용자가 로그인하지 않은 경우
			model.addAttribute("loggedIn", false);
		}
		return "header";
	}

	@GetMapping("/footer")
	public String footerPage() {
		return "footer";
	}
	
	// header 랑 footer 는 왜 들어가는거임????
	// ㄴ 이거안들어가면 header 랑 footer 못받아옴 필수임
	
	
	@GetMapping("/login")
	public String loginPage() {
		return "user/login";
	}
	
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, // username 파라미터값을 String username 변수에 저장
                         @RequestParam("password") String password) { // password 파라미터값을 String password 변수에 저장

        // 새로운 User 객체 생성 후 데이터베이스에 저장
        User user = new User();
        user.setUsername(username); //User 객체의 username 필드에 요청에서 전달된 username 값을 설정
        user.setPassword(password); //User 객체의 password 필드에 요청에서 전달된 password 값을 설정
        String changeSite = userService.selectUser(user);
        
        return changeSite;
    }
//    위에 PostMapping ("/login") 전체 동작 과정 
//    /login 경로로 POST 요청을 보냄
//    요청 데이터(username, password)가 컨트롤러 메서드로 전달
//    새로운 User 객체를 생성하고, 요청 데이터를 바인딩함
//    userService.selectUser(user)가 호출되어 비즈니스 로직을 수행
//    반환된 문자열(changeSite)에 따라 뷰를 렌더링하거나 다른 경로로 리다이렉트
    
  //마이페이지
  	@GetMapping("/mypage")
  	public String getMyPage(Model model, Principal principal) { // principal -> 현재 인증된 사용자의 정보 담고있는 객체
  		if (principal != null) { //로그인된 사용자만 다음로직 실행하도록함
  			String username = principal.getName(); // 로그인된 사용자의 정보를 가져오기 위해 username을 사용

  			// UserService를 통해 사용자 정보를 데이터베이스에서 가져옴
  			User user = userService.getUserByUsername(username);
  			Doctor doctor = doctorService.getDoctorByUsername(username);
  			
  			if(user != null && user.getUseYn().equals("N")) {
  				return "redirect:/login";
  				
  			}
  			
	         model.addAttribute("loggedIn", true);
	         model.addAttribute("username", principal.getName());
  			

  			// 사용자 정보를 모델에 추가해서 구분할꺼임. user = null이면 의사 
  			if (user == null) {
  				model.addAttribute("user", doctor);
  				model.addAttribute("isDoctor", true); //isDoctor가 true면 의사 정보를 표시
  			} else {
  				model.addAttribute("user", user);
  				model.addAttribute("isDoctor", false); //isDoctor가 false면 환자 정보를 표시
  			}
  			return "user/mypage";
  		} else {
  			return "redirect:/login"; // principal = null 이면 /mypage말고 /login 으로 이동
  			
  		}
  		
//		@GetMapping("/mypage") 전체 동작 과정 요약
//		사용자가 /mypage 경로를 요청.
//		로그인 여부 확인:
//		로그인 O: principal에서 사용자 이름을 가져옴.
//		로그인 X: /login 뷰 반환.
//		로그인된 사용자 이름(username)을 이용해 사용자 정보를 조회:
//		user로 일반 사용자 정보 확인.
//		doctor로 의사 정보 확인.
//		사용자 유형에 따라 적절한 데이터를 모델에 추가:
//		일반 사용자(isDoctor = false).
//		의사(isDoctor = true).
//		user/mypage 뷰로 사용자 정보를 전달해 마이페이지 표시.

  	}
	
	@GetMapping("/message")
	public String message() {
		return "message/message"; 
	}
	@GetMapping("/message_home")
	public String message_home() {
		return "message/message_home"; 
	}
	@GetMapping("/message_sent")
	public String message_sent() {
		return "message/message_sent"; 
	}
	@GetMapping("/message_got")
	public String message_got() {
		return "message/message_got";
	}
	
	// message
	
	@GetMapping("/history_pait")
	public String history_pait() {
		return "prescript/history_pait"; 
	}
	@GetMapping("/register")
	public String register() {
		return "prescript/register"; 
	}
	@GetMapping("/modify")
	public String modify() {
		return "prescript/modify"; 
	}
	@GetMapping("/prescript")
	public String prescript() {
		return "prescript/prescriptDetail";
	}
}
