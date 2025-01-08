package com.example.ospe.reservation.service;

import org.springframework.stereotype.Service;

import com.example.ospe.reservation.dao.ReservationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reserrep;
	
	public void deleteReservationByReservationId(Long id) {
		reserrep.deleteReservationByReservationId(id);
	}
}
