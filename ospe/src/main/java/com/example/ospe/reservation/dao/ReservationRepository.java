package com.example.ospe.reservation.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ospe.reservation.dto.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>{
	public List<Reservation> findByReservationName(String name);
	
	public List<Reservation> findByDepartment(String department);
	
	public List<Reservation> findByReservationDay(LocalDate reservationDay);
	
	public List<Reservation> findByReservationTime(LocalTime reservationTime);

	public void deleteReservationByReservationId(Long id);
	
}
