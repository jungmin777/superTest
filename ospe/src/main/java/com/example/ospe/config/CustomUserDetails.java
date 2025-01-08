package com.example.ospe.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;

public class CustomUserDetails implements UserDetails, OAuth2User {
    private final User user;
    private final Doctor doctor;

    public CustomUserDetails(User user) {
        this.user = user;
        this.doctor = null;
    }
    public CustomUserDetails(Doctor user) {
        this.doctor = user;
        this.user = null;
    }
    
    
    //Oauth2 로그인 시 사용자 속성 정보를 반환
    @Override 
    public Map<String, Object> getAttributes() { 
    	if(user == null) return doctor.getAttr(); //환자 또는 의사 호출해서 데이터 반환
        return user.getAttr();  // OAuth2 로그인일 때 사용자의 정보를 제공
    }
    
    //OAuth2 사용자 이름을 반환
    @Override
    public String getName() {
    	if(user == null) return doctor.getUsername();
        return user.getUsername();
    }
    
    
    //Security에서 인증된 사용자의 권한 목록을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { 
        List<String> roles = List.of(user == null ? doctor.getRoles().split(",") : user.getRoles().split(","));
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }
    
    //사용자의 아이디를 반환
    @Override
    public String getUsername() {
    	if(user == null) return doctor.getUsername();
        return user.getUsername();
    }
    
    //사용자의 비밀번호를 반환
    @Override
    public String getPassword() {
    	if(user == null) return doctor.getPassword();
        return user.getPassword();
    }


    // 필수
    @Override //계정의 만료 여부를 반환 -> true면 반환하므로 계정이 항상 만료되지 않은 상태로 설정
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override //계정의 잠김 여부를 반환 -> true면 계정이 항상 잠기지않은 상태로 설정
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override //계정의 비밀번호 만료여부를 반환 -> true면 비밀번호가 항상 만료되지않은 상태로 설정
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override //계정의 활성화 여부를 반환 -> true면 계정이 항상 활성화된 상태로 설정
    public boolean isEnabled() {
        return true;
    }
}
