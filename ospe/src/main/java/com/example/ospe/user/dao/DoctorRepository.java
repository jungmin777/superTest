package com.example.ospe.user.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ospe.user.dto.Doctor;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>{
   List<Doctor> findByUsername(String username); 
   
   List<Doctor> findByUsernameAndPassword(String username, String password);
   
   boolean existsByUsername(String username);
   
   // 전문 분야로 의사 조회
   List<Doctor> findBySpecialty(String specialty);
   
    // ID로 의사 단일 조회
    Optional<Doctor> findById(Long id); // Optional 사용

    // 병원 이름으로 의사 조회
    List<Doctor> findByHospitalName(String hospitalName);

    // 이메일로 의사 조회
    List<Doctor> findByEmail(String email);

}
