package com.example.ospe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean 
    //Spring Security의 HttpSecurity 객체를 사용해 요청 URL, 인증, 인가, 세션 관리 등을 설정
    //메서드가 SecurityFilterChain을 반환하도록 구성되어 있어 HTTP 보안 필터 체인을 관리
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { 
        http
            .csrf(csrf -> csrf //CSRF(Cross-Site Request Forgery) -> 공격 방지를 위한 설정
                .ignoringRequestMatchers( //여기에 지정된 경로는 CSRF 보호에서 제외 -> CSS나 JS같은거랑 보호가 필요없는 페이지 넣으셈 
                		"/css/**",  //css
                		"/logout",  //logout
                		"/patisign", //환자 회원가입
                		"/doctorsign", 	//의사 회원가입
                		"/doctor/login", //의사권한으로 로그인
                		"/pati/login", //환자권한으로 로그인
                		"/api/login",  //전체 로그인 경로
                		"/api/user/profile/**", //환자마이페이지 수정
                		"/api/doctor/profile/**", //의사 마이페이지 수정
                		"/api/user/**", //비밀번호변경 /api/user/ 
                		"/user/login", //로그인 기본페이지
                		"/mypage", //기본 마이페이지
                		"/header", //헤더
                		"/footer", //푸터
                		"/user/mypage", //환자 의사 마이페이지
                		"reservation_cust", 
                		"search_cust",
                		"search_doctor",
                		"/js/**",
                		"reservation_cust",
                		"search_cust",
                		"search_doctor",
                		"/messages/**", // 메시지 관련 경로를 하나로 묶어 처리
                        "/message_*",
                        "/message_home", 
                        "/message_sent", 
                        "/message_got",
                		"/mainpage",
                		"/board_home",
                		"/board_post",
                		"/post",
                		"/deleteBoards",
                		"/history/**"
                		)
            )
            .authorizeHttpRequests(auth -> auth //요청 경로별로 접근 권한을 정의
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll() //FORWARD는 서블릿 요청을 다른 서블릿으로 전달할 때 사용. 이러한 요청은 모든 사용자에게 허용
                .requestMatchers( //"USER" 또는 "ADMIN" 권한을 가진 사용자만 접근 가능. 그니까 너네 환자 의사 따로 보이는 페이지 등등 여기다가 집어넣으라고
                		"/api/login",
                		"/api/user/profile",
                		"/api/user/profile/**",
                		"/api/doctor/profile/**",
                		"reservation_cust",
                		"search_cust",
                		"/api/user/**",
                		"search_doctor",
                		"/mainpage",
                		"/board_home",
                		"/board_post",
                		"/post",
                		"/messages/**", // 메시지 관련 경로 처리
                        "/message_home",
                        "/message_sent",
                        "/message_got",
                        "/preList",
                        "/prescriptDetail/**",
                        "/selectPatient",
                        "/insertPrescriptView/**",
                        "/insertPrescript",
                        "/modifyPrescriptView/**",
                        "/modifyPrescript",
                        "/history/**"
                		)
                .hasAnyRole("USER", "ADMIN") //권한 USER = 환자 , ADMIN = 의사
                .anyRequest().permitAll() //anyRequest() -> 나머지 모든 요청은 인증 없이 접근 가능
            )
            .formLogin(login -> login //form 기반으로 로그인 설정
                .loginPage("/login") //로그인페이지 설정
                .loginProcessingUrl("/api/login") //로그인 처리할 경로
                .defaultSuccessUrl("/mypage", true) //로그인 성공하면 이동할 페이지인데 MainPage 완성안되서 일단 MyPage로 해놨으니 완성되면 수정해야됨
                .failureUrl("/login?error=true") //로그인 실패 시 표시메시지
                .usernameParameter("username") //form에서 username 받는 필드 이름 설정
                .passwordParameter("password") //form에서 password 받는 필드 이름 설정
                .permitAll()
            )
            .logout(logout -> logout
                .clearAuthentication(true) //로그아웃하면 인증정보 제거하는거임
                .deleteCookies("JSESSIONID") //로그아웃하면 세션쿠키 삭제하는거임
                .invalidateHttpSession(true) //HTTP 세션 무효화
                .logoutUrl("/logout")  // /logout을 요청하면 로그아웃되도록
                .logoutSuccessUrl("/login")  // 로그아웃하면 이동할 페이지
            )
            .rememberMe(rm -> rm //로그인 상태를 유지할 수 있게 해주는 거임
                .key("HelloWorldThisIsEncryptoString")  //rememberMe 토큰을 암호화 하는데 사용되는 고유 키
                .tokenValiditySeconds(3600 * 24 * 7) //토큰의 유효기간을 7일로 설정
                .userDetailsService(customUserDetailsService) //사용자 인증정보 로드할때 쓰이는거임
            )
            .oauth2Login(oauth2 -> oauth2 
                    .loginPage("/login")  // OAuth2 로그인 페이지
                    .defaultSuccessUrl("/mypage", true)  //로그인 성공하면 이동할 페이지인데 MainPage 완성안되서 일단 MyPage로 해놨으니 완성되면 수정해야됨 
                    									 //근데 Oath2 유저는 정보 받아오는게 없어서 바로 MyPage보내서 바로 작성하게 하는것도 나쁘지않을듯 
                );
        return http.getOrBuild(); //SecurityFilterChain 객체 반환
    }
}
