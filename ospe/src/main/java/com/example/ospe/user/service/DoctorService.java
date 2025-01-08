package com.example.ospe.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ospe.user.dao.DoctorRepository;
import com.example.ospe.user.dto.Doctor;

import jakarta.transaction.Transactional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository, PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 비밀번호 확인: 원본 비밀번호와 암호화된 비밀번호 비교
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public Optional<Doctor> findByUsername(String username) {
        return doctorRepository.findByUsername(username).stream().findFirst();
    }
    
    // ID로 Doctor 조회 (필요 시 추가)
    public Optional<Doctor> findById(Long id) {
        return doctorRepository.findById(id);
    }
    
    @Transactional
    public void saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);  // 의사 정보 저장
    }

    // 의사 정보를 가져오는 메서드
    public Doctor getDoctorByUsername(String username) {
        List<Doctor> doctors = doctorRepository.findByUsername(username);
        if (doctors.isEmpty()) {
           return null;
        }
        return doctors.get(0); // 첫 번째 의사 반환
    }

    // 의사 정보를 업데이트하는 메서드
    public Doctor updateDoctorProfile(Doctor updatedDoctor) {
        List<Doctor> doctors = doctorRepository.findByUsername(updatedDoctor.getUsername());
        if (doctors.isEmpty()) {
            return null;
        }

        Doctor existingDoctor = doctors.get(0); // 첫 번째 의사 선택

        // 수정할 필드를 업데이트
        existingDoctor.setName(updatedDoctor.getName());
        existingDoctor.setBirthDate(updatedDoctor.getBirthDate());
        existingDoctor.setHospitalName(updatedDoctor.getHospitalName());
        existingDoctor.setHospitalAddress(updatedDoctor.getHospitalAddress());
        existingDoctor.setSpecialty(updatedDoctor.getSpecialty());
        existingDoctor.setEmail(updatedDoctor.getEmail());
        existingDoctor.setUpdatedAt(LocalDateTime.now()); // 업데이트 시각 설정

        return doctorRepository.save(existingDoctor);
    }

    // 의사 계정을 삭제하는 메서드
    public void deleteDoctor(String username) {
        List<Doctor> doctors = doctorRepository.findByUsername(username);
        if (doctors.isEmpty()) {
            throw new RuntimeException("Doctor not found");
        }
        
        doctorRepository.delete(doctors.get(0)); // 첫 번째 의사 삭제
    }

    // 의사 이름과 암호화된 비밀번호를 DB에서 찾기 위한 메서드
    public boolean selectDoctor(Doctor doctor) {
        // 비밀번호 비교는 암호화된 값으로 해야 함
        List<Doctor> doctorList = doctorRepository.findByUsername(doctor.getUsername());
        String password = null;
        for (Doctor item : doctorList) {
            password = item.getPassword();
            System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb " + password);
        }
        boolean matches = passwordEncoder.matches(doctor.getPassword().toString(), password);
        return matches;
    }
    
    //중복확인 메서드
    public boolean isUsernameTaken(String username) {
        return doctorRepository.existsByUsername(username);
    }
    
    
    // 변경한 비밀번호 암호화
    public void changePassword(String username, String newPassword) {
        List<Doctor> doctors = doctorRepository.findByUsername(username); // Doctor로 수정
        if (doctors == null || doctors.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 여러 의사 처리 (모든 의사 비밀번호 변경)
        for (Doctor doctor : doctors) {
            // 새 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(newPassword);
            doctor.setPassword(encodedPassword); // Doctor 엔티티로 수정

            doctorRepository.save(doctor); // 암호화된 비밀번호 저장
        }
    } 
}


   