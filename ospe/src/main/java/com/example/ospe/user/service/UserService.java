package com.example.ospe.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ospe.user.dao.UserRepository;
import com.example.ospe.user.dto.User;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 비밀번호 확인: 원본 비밀번호와 암호화된 비밀번호 비교
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);  // 사용자 정보 저장
    }
    // 사용자 이름으로 사용자 정보를 조회하는 메서드
    public List<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 사용자 id로 사용자 정보를 조회하는 메서드 (추가된 메서드)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);  // id로 사용자 조회
    }
    
    
 // 사용자 정보를 가져오는 메서드
    public User getUserByUsername(String username) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0); // 첫 번째 사용자 반환
    }
    
	// message에서 참조하기위해서 추가함
	public User findFirstUserByUsername(String username) {
		if (username == null || username.isEmpty()) {
			System.out.println("Invalid username: " + username);
			return null; // username이 null이거나 비어있으면 null 반환
		}

		List<User> users = userRepository.findByUsername(username); // List<User> 반환
		if (users == null || users.isEmpty()) {
			System.out.println("User not found for username: " + username);
			return null; // 사용자 없음
		}
		return users.get(0); // 첫 번째 사용자 반환
	}

    // 사용자 정보를 업데이트하는 메서드
    public User updateUserProfile(User updatedUser) {
//    	System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+updatedUser.getUsername()); //하 오류찾다가 여기였더라
        List<User> users = userRepository.findByUsername(updatedUser.getUsername());
        if (users.isEmpty()) {
            return null;
        }

        User existingUser = users.get(0); // 첫 번째 사용자 선택

        // 수정할 필드를 업데이트
        existingUser.setName(updatedUser.getName());
        existingUser.setBirthDate(updatedUser.getBirthDate());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setHeight(updatedUser.getHeight());
        existingUser.setWeight(updatedUser.getWeight());
        existingUser.setDiseases(updatedUser.getDiseases());
        existingUser.setAllergies(updatedUser.getAllergies());
        existingUser.setMedications(updatedUser.getMedications());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setBloodType(updatedUser.getBloodType());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(existingUser);
    }

    // 사용자 계정을 삭제하는 메서드
    public void deleteUser(String username) {
        List<User> users = userRepository.findByUsername(username);
        if (users.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        userRepository.delete(users.get(0)); // 첫 번째 사용자 삭제
    }
    
    

    // 사용자 이름과 암호화된 비밀번호를 DB에서 찾기 위한 메서드
    public String selectUser(User user) {
        // 비밀번호 비교는 암호화된 값으로 해야 함
        // 예시: username과 password를 통해 사용자를 조회
        List<User> userList = userRepository.findByUsername(user.getUsername());
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        String password = null;
        for(User item : userList) {
        	password = item.getPassword();
        	if(item.getUseYn().equals("N")) {
                System.out.println(item.getUseYn()+"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                

        		return "redirect:/login"; 
        	}
        }
        boolean matches = passwordEncoder.matches(user.getPassword().toString(), password); 
        if(matches == true) {
        	return "redirect:/home";
        }else {
        	return "redirect:/login";
        }
    	
    }
    
    
    
    //변경한 비밀번호 암호화
    public void changePassword(String username, String newPassword) {
        List<User> users = userRepository.findByUsername(username);
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 여러 사용자 처리 (모든 사용자 비밀번호 변경)
        for (User user : users) {
            // 새 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            userRepository.save(user); // 암호화된 비밀번호 저장
        }
    }

    //중복확인 메서드
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }
    
    
    
}
