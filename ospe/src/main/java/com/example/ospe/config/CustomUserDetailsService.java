package com.example.ospe.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.ospe.user.dao.DoctorRepository;
import com.example.ospe.user.dao.UserRepository;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;

@Service
public class CustomUserDetailsService extends DefaultOAuth2UserService implements UserDetailsService {

    @Autowired
    UserRepository userrep;
    @Autowired
    DoctorRepository docrep;

    
    
    //사용자 ID 조회 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userrep.findByUsername(username); //USER 목록을 조회해서 있으면 환자 , null이면 의사 계정을 찾음
        if (users.isEmpty()) {
        	List<Doctor> doctors = docrep.findByUsername(username);
        	if(doctors.isEmpty()) throw new UsernameNotFoundException(username + " 찾을 수 없음"); //사용자가 없으면 UsernameNotFoundException 던지고 
        	return new CustomUserDetails(doctors.get(0)); //CustomUserDetails로 래핑해서 반환
        }
        return new CustomUserDetails(users.get(0));
    }

    
    
    
    
    
    
    
    
    
    //OAuth2 로그인 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthuser = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        User user = new User();
        
        //네이버
        if (provider.equals("naver")) {
            Map<String, Object> attr = (Map<String, Object>) oauthuser.getAttributes().get("response");
            List<User> users = userrep.findByUsername(((String) attr.get("id")) + "_" + provider);
            if (!users.isEmpty()) {
                user = users.get(0);
            } else {
                user.setUsername(provider);
                user.setPassword("비밀번호");  // setPass를 setPassword로 수정
                userrep.save(user);
            }
            user.setAttr(attr);
            
            
//        //카카오
//        } else if (provider.equals("kakao")) {
//            Map<String, Object> attr = (Map<String, Object>) oauthuser.getAttributes();
//            List<User> users = userrep.findByUsername((attr.get("id").toString()) + "_" + provider);
//            if (!users.isEmpty()) {
//                user = users.get(0);
//            } else {
//                user.setName((attr.get("id").toString()) + "_" + provider);
//                user.setPassword("비밀번호");  // setPass를 setPassword로 수정
//                userrep.save(user);
//            }
//            user.setAttr(attr);
//            
//        //구글
//        } else if (provider.equals("google")) {
//            Map<String, Object> attr = (Map<String, Object>) oauthuser.getAttributes();
//            List<User> users = userrep.findByUsername(attr.get("sub").toString() + "_" + provider);
//            if (!users.isEmpty()) {
//                user = users.get(0);
//            } else {
//                user.setName(attr.get("sub").toString() + "_" + provider);
//                user.setPassword("비밀번호");  // setPass를 setPassword로 수정
//                userrep.save(user);
//            }
//            user.setAttr(attr);
        }

        return new CustomUserDetails(user); //사용자의 정보를 CustomUserDetails로 래핑해서 반환
    }
}
