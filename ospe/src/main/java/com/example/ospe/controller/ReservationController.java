package com.example.ospe.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ospe.config.CustomUserDetails;
import com.example.ospe.reservation.dao.ReservationRepository;
import com.example.ospe.reservation.dto.Headerlogin;
import com.example.ospe.reservation.dto.Reservation;
import com.example.ospe.reservation.service.ReservationService;
import com.example.ospe.user.dao.DoctorRepository;
import com.example.ospe.user.dao.UserRepository;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;

@Controller
public class ReservationController {
	
	@Autowired
	ReservationRepository reserrep;
	
	@Autowired
	DoctorRepository doctorrep;
	
	@Autowired
	UserRepository userrep;
	
	@Autowired
	private ReservationService reserive;
	
	@Autowired
	Headerlogin keep; // 로그인 유지 재사용 Headerlogin 클래스

	@ModelAttribute //모든 매핑에 추가할 코드
    public void addAttributes(Model model, Principal principal) {
        keep.headerlogin(model, principal); //로그인 유지 
    }
	
	// 예약 조회 메서드(데이터 불러 model)
	@GetMapping("/search_cust")
	public String SearchButton(@AuthenticationPrincipal CustomUserDetails userDetails, Model model,
	                           @RequestParam(value = "searchselect", required = false) String searchselect,
	                           @RequestParam(value = "searchinput", required = false) String searchinput,
	                           @RequestParam(value = "page", defaultValue = "1") int page, Principal principal) {

	    // 로그인한 사용자의 이름 가져오기
	    String username = userDetails.getName();
	    
	    int pageSize = 10; // 한 페이지에 표시할 게시글 개수
	    int paginationSize = 10; // 페이지 번호 최대 표시 개수

	    // 해당 사용자의 모든 예약 가져오기
	    List<Reservation> userReservations = reserrep.findAll(Sort.by(Sort.Direction.DESC, "reservationDay"))
	            .stream()
	            .filter(res -> res.getReservationName().equals(username))
	            .collect(Collectors.toList());

	    // 검색 조건 적용
	    if (searchselect != null && !searchselect.isEmpty() && searchinput != null && !searchinput.isEmpty()) {
	        switch (searchselect) {
	            case "진료과목":
	                userReservations = userReservations.stream()
	                        .filter(res -> res.getDepartment().contains(searchinput))
	                        .collect(Collectors.toList());
	                break;
	            case "예약일":
	                try {
	                    LocalDate reservationDay = LocalDate.parse(searchinput);
	                    userReservations = userReservations.stream()
	                            .filter(res -> res.getReservationDay().equals(reservationDay))
	                            .collect(Collectors.toList());
	                } catch (DateTimeParseException e) {
	                    // 잘못된 날짜 형식 처리
	                    model.addAttribute("errorMessage", "예약일은 올바른 날짜 형식으로 입력해주세요 (예: yyyy-MM-dd).");
	                    return "reservation/search_cust";
	                }
	                break;
	            default:
	                userReservations = new ArrayList<>();
	                break;
	        }
	    }

	    int totalreservation = userReservations.size();
	    int totalpages = (int) Math.ceil((double) totalreservation / pageSize);
	    
	    int startIndex = (page-1) * pageSize;
	    int endIndex = Math.min(startIndex + pageSize, totalreservation);
	    
	    List<Reservation> paginationreservation = userReservations.subList(startIndex, endIndex);

	    int currentRangeStart = ((page-1) / paginationSize) * paginationSize + 1;
	    int currentRangeEnd = Math.min(currentRangeStart + paginationSize - 1, totalpages);
	    
	    // 모델에 데이터 추가
	    model.addAttribute("boards", paginationreservation);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalpages);
	    model.addAttribute("startPage", currentRangeStart); // 수정
	    model.addAttribute("endPage", currentRangeEnd); // 수정
	    model.addAttribute("searchselect", searchselect); // 검색 선택
	    model.addAttribute("searchinput", searchinput);  // 검색 입력

	    return "reservation/search_cust";
	}
	
	
	// 예약 처리 메소드 (POST 요청)
	 @PostMapping("/search_cust") 
	 public String ReservationResult(
		@RequestParam("Department") String department,
		@RequestParam("ResultReservationDay") String reservationDay,
		@RequestParam("ResultReservationTime") String reservationTime,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {		 
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy / MM / dd");
		 LocalDate localDate = LocalDate.parse(reservationDay, formatter); // 날짜 문자열을 LocalDate로 변환

		 DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm");
		 LocalTime localTime = LocalTime.parse(reservationTime, formatter2); // 시간 문자열을 LocalDate로 변환
		 
		 String username = userDetails.getName();  // 로그인한 username을 가져옴
		 
		// 중복 여부 확인
		    boolean exists = reserrep.findAll().stream()
		        .anyMatch(reservation -> 
		            reservation.getReservationName().equals(username) &&
		            reservation.getDepartment().equals(department) &&
		            reservation.getReservationDay().equals(localDate) &&
		            reservation.getReservationTime().equals(localTime)
		        );
		 
		 if (!exists) { // 중복되지 않으면 예약 정보 저장
	        List<Doctor> doctors = doctorrep.findBySpecialty(department); // department로 진료과의 정보를 의사리포지토리에서 불러오기
	        if (doctors == null || doctors.isEmpty()) {
	            throw new IllegalArgumentException("해당 진료과에 의사가 없습니다.");
	        }
	        
	        // 의사별 예약 건수를 확인하여 예약이 가장 적은 의사를 찾기
	        Doctor leastBookedDoctor = doctors.stream()
	            .min(Comparator.comparingInt(doctor -> doctor.getReservation().size())) // 예약이 가장 적은 의사 선택
	            .orElseThrow(() -> new IllegalArgumentException("예약 가능한 의사가 없습니다."));
	        
	        
	        List<User> users = userrep.findByUsername(username); // UserRepository에서 사용자 정보를 가져오는 부분
	        if (users == null || users.isEmpty()) {
	        	throw new IllegalArgumentException("해당 진료과에 환자가 없습니다.");
	        }
	        User user = users.get(0);
	        
	        String useruser = user.getName();	      
	        
			 Reservation reservation = new Reservation(
		        	    null,                // 예약번호 (null로 설정, 자동 생성됨)
		        	    username,            // 사용자 아이디
		        	    useruser,
		        	    department,          // 진료과
		        	    localDate,      // 예약 날짜
		        	    localTime,      // 예약 시간
		        	    user     // 사용자 객체
		        	);
		 
	        	// 저장
			 reservation = reserrep.save(reservation);
			 leastBookedDoctor.addReservation(reservation);
			 doctorrep.save(leastBookedDoctor);

		 }

		 return "redirect:/search_cust"; //포스트매핑에서 데이터 입력 -> 겟매핑으로 이동
	 }
	 
	 @PostMapping("/search_cust/{id}/delete")
	 public String deleteReservation(@PathVariable("id") Long id) {
		 reserive.deleteReservationByReservationId(id);
		 return "redirect:/search_cust";
	 }
	 
	 // 의사예약 조회 페이지
	 @GetMapping("/search_doctor")
	 public String Search_doctor(@AuthenticationPrincipal CustomUserDetails userDetails, Model model,
	                              @RequestParam(value = "searchselect", required = false) String searchselect,
	                              @RequestParam(value = "searchinput", required = false) String searchinput,
	                              @RequestParam(value = "page", defaultValue = "1") int page) {

	     // 로그인한 사용자의 이름을 가져옵니다.
	     String username = userDetails.getName();
	     List<Doctor> doctor = doctorrep.findByUsername(username);
	     Doctor doctors = doctor.get(0);  // 첫 번째 의사 선택
	     
	     int pageSize = 10; // 한 페이지에 표시할 게시글 개수
		 int paginationSize = 10; // 페이지 번호 최대 표시 개수

	     // 모든 예약을 가져오고 내림차순 정렬
	     List<Reservation> reservations = doctors.getReservation()
	             .stream()
	             .sorted((r1, r2) -> r2.getReservationDay().compareTo(r1.getReservationDay())) // 예약일 기준 내림차순 정렬
	             .collect(Collectors.toList());

	     // 검색 조건이 없으면 기본 페이지 반환
	     List<Reservation> doctorReservations = new ArrayList<>(reservations);
	     if (searchselect != null && !searchselect.isEmpty() && searchinput != null && !searchinput.isEmpty()) {
	         try {
	             // 검색 필터링 처리
	             switch (searchselect) {
	                 case "예약번호":
	                     Long reservationId = Long.parseLong(searchinput);
	                     doctorReservations = doctorReservations.stream()
	                             .filter(res -> res.getReservationId().equals(reservationId))
	                             .collect(Collectors.toList());
	                     break;
	                 case "회원명":
	                	 doctorReservations = doctorReservations.stream()
	                             .filter(res -> res.getReservationUsername().contains(searchinput))
	                             .collect(Collectors.toList());
	                     break;
	                 case "예약일":
	                     LocalDate reservationDay = LocalDate.parse(searchinput);
	                     doctorReservations = doctorReservations.stream()
	                             .filter(res -> res.getReservationDay().equals(reservationDay))
	                             .collect(Collectors.toList());
	                     break;
	                 default:
	                	 doctorReservations = new ArrayList<>();
	             }
	         } catch (NumberFormatException e) {
	             model.addAttribute("errorMessage", "예약번호는 숫자만 입력 가능합니다.");
	             return "reservation/search_doctor";
	         } catch (DateTimeParseException e) {
	             model.addAttribute("errorMessage", "예약일은 올바른 날짜 형식으로 입력해주세요 (예: yyyy-MM-dd).");
	             return "reservation/search_doctor";
	         }
	     }
	     
	     int totalreservation = doctorReservations.size();
		 int totalpages = (int) Math.ceil((double) totalreservation / pageSize);
		 
		 int startIndex = (page-1) * pageSize;
		 int endIndex = Math.min(startIndex + pageSize, totalreservation);

	     // 현재 페이지에 해당하는 예약
	     List<Reservation> paginationreservation = doctorReservations.subList(startIndex, endIndex);

	     // 페이지네이션 범위 계산
	     int currentRangeStart = ((page-1) / paginationSize) * paginationSize + 1;
		    int currentRangeEnd = Math.min(currentRangeStart + paginationSize - 1, totalpages);

		 // 필터링된 예약을 모델에 추가하여 뷰에 전달
		    model.addAttribute("doctorboards", paginationreservation);
		    model.addAttribute("currentPage", page);
		    model.addAttribute("totalPages", totalpages);
		    model.addAttribute("startPage", currentRangeStart); // 수정
		    model.addAttribute("endPage", currentRangeEnd); // 수정
		    model.addAttribute("searchselect", searchselect); // 검색 선택
		    model.addAttribute("searchinput", searchinput);  // 검색 입력

	     return "reservation/search_doctor";
	 }
	 
	 // 진료 예약 페이지로 이동하는 메소드
	 @GetMapping("/reservation_cust")
	 public String Reservation_cust() {	
		 return "reservation/reservation_cust"; 
	 }
}