package com.example.ospe.prescript.dto;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="prescript")
@SequenceGenerator(
		allocationSize = 1,
		initialValue = 0,
		name = "PrescriptSeq",
		sequenceName = "PrescriptSeq"
	)
public class Prescript {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PrescriptSeq")
	private Long prescriptNum; // PK
	
	private String doctorName; // 의사명
	private int patientNum; // 환자번호
	private String patientName; // 환자명
	//private String patientPhone; // 환자 전화번호 (삭제?
	private Date prescriptDate; // 처방일
	private String department; // 진료과
	private String medications; // 약 종류
	private String oneTimeHowMany; // 한번섭취 얼마나
	private String takeDate; // 복용일
	private String howToEat; // 복용법
	private String hospitalAddress; // 병원주소
	private String hospitalName; // 병원명
	private String doctorAdvice; // 의사 조언
	
	private String allergies;
	private String diseases;
	private String oneDayHowMany; // 하루에 몇번 먹는지
	private String injectionHistory; // 주사제 투여 내역	
	
}
