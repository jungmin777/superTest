package com.example.ospe.controller;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ospe.reservation.dto.Headerlogin;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

	@Autowired
	Headerlogin keep; // 로그인 유지 재사용 Headerlogin 클래스

	@ModelAttribute //모든 매핑에 추가할 코드
    public void addAttributes(Model model, Principal principal) {
        keep.headerlogin(model, principal); //로그인 유지 
    }
	
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // HTTP 상태 코드 가져오기 (예: 404, 500 등)
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        
        // 오류 메시지 모델에 추가
        String errorMessage;
        if (statusCode != null) {
        	System.out.println(statusCode);
            switch (statusCode) {
                case 404:
                    errorMessage = "페이지를 찾을 수 없습니다.";
                    break;
                case 500:
                    errorMessage = "서버 오류가 발생했습니다.";
                    break;
                default:
                    errorMessage = "알 수 없는 오류가 발생했습니다.";
                    break;
            }
        } else {
            errorMessage = "알 수 없는 오류가 발생했습니다.";
        }

        // 모델에 오류 메시지 추가
        model.addAttribute("errorMessage", errorMessage);
        
        // 사용자 정의 에러 페이지로 리디렉션
        return "error/error"; // error.html 페이지로 이동
    }

}
