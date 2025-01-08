package com.example.ospe.reservation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.example.ospe.user.dto.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Reservation")
@SequenceGenerator(
	allocationSize = 1,
	initialValue = 1,
	name = "ReservationSeq",
	sequenceName = "ReservationSeq"
)
public class Reservation {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "ReservationSeq"
	)
	private Long reservationId;
	
	// 환자 아이디
	private String reservationName;
	
	// 환자 성명
	private String reservationUsername;
	
	// 진료과목
	private String department;
	
	// 예약일자
	private LocalDate reservationDay;
	
	// 예약시간
	private LocalTime reservationTime;
    
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
