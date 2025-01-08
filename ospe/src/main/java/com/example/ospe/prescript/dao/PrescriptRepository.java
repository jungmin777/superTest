package com.example.ospe.prescript.dao;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ospe.prescript.dto.Prescript;

@Repository
@Scope("singleton")
public interface PrescriptRepository extends JpaRepository<Prescript, Long>{
	
	public List<Prescript> findByPatientName(String name); // 환자명 -> 동명이인 있을수 있음
	public List<Prescript> findByPatientNum(long num); // 환자번호로 가져오기 -> 외래키지만 겹치친 않음
	public Prescript findByPrescriptNum(int preNum);
	public List<Prescript> findByDoctorName(String n);
	List<Prescript> findByDoctorNameAndPatientNameContaining(String doctorName, String patientName);
	List<Prescript> findByPatientNumAndPatientNameContaining(Long patientNum, String patientName);
	public List<Prescript> findByDepartment( String department);
	public List<Prescript> findByDepartmentOrderByPrescriptNumDesc(String specialty);

	
}
