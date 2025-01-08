package com.example.ospe.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ospe.config.CustomUserDetails;
import com.example.ospe.message.dao.MessageRepository;
import com.example.ospe.message.dto.Message;
import com.example.ospe.reservation.dto.Headerlogin;
import com.example.ospe.user.dao.DoctorRepository;
import com.example.ospe.user.dao.UserRepository;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;
import com.example.ospe.user.service.DoctorService;
import com.example.ospe.user.service.UserService;

@Controller
@RequestMapping("/messages") // 모든 경로에 /messages가 자동으로 추가됨
public class MessageController {

	@Autowired
	private MessageRepository messageRepo;

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DoctorService doctorService;
	
	@Autowired
	private DoctorRepository doctorRepo;

	@Autowired
	Headerlogin keep; // 로그인 유지 재사용 Headerlogin 클래스

	@ModelAttribute
	public void addAttributes(Model model, Principal principal) {
		keep.headerlogin(model, principal); // 로그인 유지
	}
	
	
	@GetMapping("/message_sent")
	public String messageSent(
	    @AuthenticationPrincipal CustomUserDetails userDetails,
	    @RequestParam(defaultValue = "1") int page,
	    @RequestParam(value = "searchType", required = false) String searchType,
	    @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
	    Model model
	) {
	    String username = userDetails.getUsername();
	    int pageSize = 10;
	    int paginationSize = 10;

	    // 메시지 가져오기
	    List<Message> sentMessages = messageRepo.findBySender(username);

	    // Doctor 정보 확인
	    Doctor doctor = doctorService.getDoctorByUsername(username);
	    if (doctor != null) {
	        sentMessages.addAll(messageRepo.findBySender(doctor.getUsername()));
	    }

	    // 메시지 송신자 및 수신자의 유형 확인
	    Map<String, String> senderTypeMap = new HashMap<>();
	    Map<String, String> receiverTypeMap = new HashMap<>();

	    for (Message message : sentMessages) {
	        if (!senderTypeMap.containsKey(message.getSender())) {
	            senderTypeMap.put(message.getSender(),
	                doctorService.getDoctorByUsername(message.getSender()) != null ? "Doctor" : "User");
	        }
	        if (!receiverTypeMap.containsKey(message.getReceiver())) {
	            receiverTypeMap.put(message.getReceiver(),
	                doctorService.getDoctorByUsername(message.getReceiver()) != null ? "Doctor" : "User");
	        }
	    }

	    // 검색 조건 처리
	    if (searchType != null && searchKeyword != null) {
	        if (searchType.equals("titleContent")) {
	            sentMessages = sentMessages.stream()
	                .filter(message -> message.getTitle().contains(searchKeyword) || message.getContent().contains(searchKeyword))
	                .collect(Collectors.toList());
	        } else if (searchType.equals("receiver")) {
	            sentMessages = sentMessages.stream()
	                .filter(message -> message.getReceiver().contains(searchKeyword))
	                .collect(Collectors.toList());
	        }
	    }

	    // 중복 제거 및 정렬
	    Set<Message> uniqueMessages = new HashSet<>(sentMessages);
	    List<Message> paginatedMessages = new ArrayList<>(uniqueMessages);
	    paginatedMessages.sort(Comparator.comparingLong(Message::getId).reversed());

	    // 날짜 포맷 추가
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd HH:mm");
	    paginatedMessages.forEach(message -> message.setSendDateFormatted(message.getSendDate().format(formatter)));

	    // 페이지네이션
	    int totalMessages = paginatedMessages.size();
	    int totalPages = (int) Math.ceil((double) totalMessages / pageSize);
	    int startIndex = (page - 1) * pageSize;
	    int endIndex = Math.min(startIndex + pageSize, totalMessages);

	    List<Message> currentPageMessages = paginatedMessages.subList(startIndex, endIndex);

	    // 모델에 데이터 추가
	    model.addAttribute("messages", currentPageMessages);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("startPage", ((page - 1) / paginationSize) * paginationSize + 1);
	    model.addAttribute("endPage", Math.min(((page - 1) / paginationSize) * paginationSize + paginationSize, totalPages));
	    model.addAttribute("searchType", searchType);
	    model.addAttribute("searchKeyword", searchKeyword);
	    model.addAttribute("senderTypeMap", senderTypeMap);
	    model.addAttribute("receiverTypeMap", receiverTypeMap);

	    return "message/message_sent";
	}


	@GetMapping("/message_got")
	public String messageGot(
	    @AuthenticationPrincipal CustomUserDetails userDetails,
	    @RequestParam(defaultValue = "1") int page,
	    @RequestParam(value = "searchType", required = false) String searchType,
	    @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
	    Model model
	) {
	    String username = userDetails.getUsername();
	    int pageSize = 10; // 한 페이지에 표시할 메시지 개수
	    int paginationSize = 10; // 페이지 번호 최대 표시 개수

	    List<Message> receivedMessages;

	    // 현재 사용자 정보 확인 (User 또는 Doctor)
	    if (doctorService.getDoctorByUsername(username) != null) {
	        // Doctor 계정일 경우
	        receivedMessages = messageRepo.findByReceiver(username);
	    } else {
	        // User 계정일 경우
	        receivedMessages = messageRepo.findByReceiver(username);
	    }

	    // 검색 조건 처리
	    if (searchType != null && searchKeyword != null) {
	        if (searchType.equals("titleContent")) {
	            receivedMessages = receivedMessages.stream()
	                .filter(message -> message.getTitle().contains(searchKeyword) || message.getContent().contains(searchKeyword))
	                .collect(Collectors.toList());
	        } else if (searchType.equals("sender")) {
	            receivedMessages = receivedMessages.stream()
	                .filter(message -> message.getSender().contains(searchKeyword))
	                .collect(Collectors.toList());
	        }
	    }

	    // 메시지 송신자 및 수신자의 유형 확인
	    Map<String, String> senderTypeMap = new HashMap<>();
	    Map<String, String> receiverTypeMap = new HashMap<>();

	    for (Message message : receivedMessages) {
	        if (!senderTypeMap.containsKey(message.getSender())) {
	            senderTypeMap.put(message.getSender(),
	                doctorService.getDoctorByUsername(message.getSender()) != null ? "Doctor" : "User");
	        }
	        if (!receiverTypeMap.containsKey(message.getReceiver())) {
	            receiverTypeMap.put(message.getReceiver(),
	                doctorService.getDoctorByUsername(message.getReceiver()) != null ? "Doctor" : "User");
	        }
	    }

	    // 내림차순 정렬
	    receivedMessages.sort(Comparator.comparingLong(Message::getId).reversed());

	    // 전체 메시지 개수 및 페이지네이션 계산
	    int totalMessages = receivedMessages.size();
	    int totalPages = (int) Math.ceil((double) totalMessages / pageSize);
	    int startIndex = (page - 1) * pageSize;
	    int endIndex = Math.min(startIndex + pageSize, totalMessages);

	    List<Message> paginatedMessages = receivedMessages.subList(startIndex, endIndex);

	    // 날짜 포맷 추가
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd HH:mm");
	    paginatedMessages.forEach(message -> message.setSendDateFormatted(
	        message.getSendDate().format(formatter)
	    ));

	    // 페이지네이션 범위 계산
	    int currentRangeStart = ((page - 1) / paginationSize) * paginationSize + 1;
	    int currentRangeEnd = Math.min(currentRangeStart + paginationSize - 1, totalPages);

	    // 모델에 데이터 추가
	    model.addAttribute("messages", paginatedMessages);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("startPage", currentRangeStart);
	    model.addAttribute("endPage", currentRangeEnd);
	    model.addAttribute("searchType", searchType);
	    model.addAttribute("searchKeyword", searchKeyword);
	    model.addAttribute("senderTypeMap", senderTypeMap);
	    model.addAttribute("receiverTypeMap", receiverTypeMap);

	    return "message/message_got";
	}

	
	
	@GetMapping("/message_home")
	public String messageHome(
	    @AuthenticationPrincipal CustomUserDetails userDetails, 
	    @RequestParam(defaultValue = "1") int page,
	    @RequestParam(value = "searchType", required = false) String searchType,
	    @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
	    Model model
	) {
	    String username = userDetails.getUsername();
	    int pageSize = 10;
	    int paginationSize = 10;

	    // 메시지 가져오기
	    List<Message> sentMessages = messageRepo.findBySender(username);
	    List<Message> receivedMessages = messageRepo.findByReceiver(username);

	    // Doctor 정보가 있을 경우 추가로 메시지 가져오기
	    Doctor doctor = doctorService.getDoctorByUsername(username);
	    if (doctor != null) {
	        sentMessages.addAll(messageRepo.findBySender(doctor.getUsername()));
	        receivedMessages.addAll(messageRepo.findByReceiver(doctor.getUsername()));
	    }

	    // 보낸 메시지와 받은 메시지 합치기 및 중복 제거
	    Set<Message> allMessagesSet = new HashSet<>();
	    allMessagesSet.addAll(sentMessages);
	    allMessagesSet.addAll(receivedMessages);

	    List<Message> allMessages = new ArrayList<>(allMessagesSet);

	    // 메시지 송신자 및 수신자의 유형 확인
	    Map<String, String> senderTypeMap = new HashMap<>();
	    Map<String, String> receiverTypeMap = new HashMap<>();

	    for (Message message : allMessages) {
	        if (!senderTypeMap.containsKey(message.getSender())) {
	            senderTypeMap.put(message.getSender(),
	                doctorService.getDoctorByUsername(message.getSender()) != null ? "Doctor" : "User");
	        }
	        if (!receiverTypeMap.containsKey(message.getReceiver())) {
	            receiverTypeMap.put(message.getReceiver(),
	                doctorService.getDoctorByUsername(message.getReceiver()) != null ? "Doctor" : "User");
	        }
	    }

	    // 검색 조건 적용
	    if (searchType != null && searchKeyword != null && !searchKeyword.isEmpty()) {
	        switch (searchType) {
	            case "titleContent":
	                allMessages = allMessages.stream()
	                    .filter(message -> message.getTitle().contains(searchKeyword) || 
	                                       message.getContent().contains(searchKeyword))
	                    .collect(Collectors.toList());
	                break;
	            case "senderReceiver":
	                allMessages = allMessages.stream()
	                    .filter(message -> message.getSender().contains(searchKeyword) || 
	                                       message.getReceiver().contains(searchKeyword))
	                    .collect(Collectors.toList());
	                break;
	            default:
	                // 검색 조건이 없거나 잘못된 경우 그대로 진행
	        }
	    }

	    // 내림차순 정렬
	    allMessages.sort(Comparator.comparingLong(Message::getId).reversed());

	    // 페이지네이션
	    int totalMessages = allMessages.size();
	    int totalPages = (int) Math.ceil((double) totalMessages / pageSize);
	    int startIndex = (page - 1) * pageSize;
	    int endIndex = Math.min(startIndex + pageSize, totalMessages);

	    List<Message> paginatedMessages = allMessages.subList(startIndex, endIndex);

	    // 날짜 포맷 추가
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd HH:mm");
	    paginatedMessages.forEach(message -> message.setSendDateFormatted(
	        message.getSendDate().format(formatter)
	    ));

	    // 페이지네이션 범위 계산
	    int currentRangeStart = ((page - 1) / paginationSize) * paginationSize + 1;
	    int currentRangeEnd = Math.min(currentRangeStart + paginationSize - 1, totalPages);

	    // 모델에 데이터 추가
	    model.addAttribute("messages", paginatedMessages);
	    model.addAttribute("senderTypeMap", senderTypeMap);
	    model.addAttribute("receiverTypeMap", receiverTypeMap);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("startPage", currentRangeStart);
	    model.addAttribute("endPage", currentRangeEnd);
	    model.addAttribute("searchType", searchType);
	    model.addAttribute("searchKeyword", searchKeyword);

	    return "message/message_home";
	}
	
	@GetMapping("/message/{id}")
	public String getMessageDetail(@PathVariable Long id, 
	                               @AuthenticationPrincipal CustomUserDetails userDetails, 
	                               Model model) {
	    Optional<Message> optionalMessage = messageRepo.findById(id);
	    if (optionalMessage.isPresent()) {
	        Message message = optionalMessage.get();

	        // 날짜 포맷 추가
	        String formattedDate = message.getSendDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
	        message.setSendDateFormatted(formattedDate);
	        
	        // 수신자가 의사인지 확인하고 진료과 포함
	        Doctor doctorReceiver = doctorService.getDoctorByUsername(message.getReceiver());
	        if (doctorReceiver != null) {
	            model.addAttribute("receiverSpecialty", doctorReceiver.getSpecialty());
	        }
	        
	        // 송신자가 의사인지 확인하고 진료과 포함
	        Doctor doctorSender = doctorService.getDoctorByUsername(message.getSender());
	        if (doctorSender != null) {
	        	model.addAttribute("senderSpecialty", doctorSender.getSpecialty());
	        }

	        // 보낸 사람 및 받은 사람의 역할 확인 (User 또는 Doctor)
	        String senderType = userService.getUserByUsername(message.getSender()) != null ? "User" : "Doctor";
	        String receiverType = userService.getUserByUsername(message.getReceiver()) != null ? "User" : "Doctor";

	        // 현재 로그인된 사용자가 송신자인지 확인
	        boolean isSender = message.getSender().equals(userDetails.getUsername());

	        // 답장 시 수신자 설정
	        String replyReceiver = isSender ? message.getReceiver() : message.getSender();
	        
	        System.out.println("Sender: " + message.getSender());
	        System.out.println("Receiver: " + message.getReceiver());


	        model.addAttribute("message", message);
	        model.addAttribute("senderType", senderType);
	        model.addAttribute("receiverType", receiverType);
	        model.addAttribute("isSender", isSender);
	        model.addAttribute("replyReceiver", replyReceiver);
	        
	        System.out.println("Message: " + message);
	        System.out.println("Is Sender: " + isSender);
	        System.out.println("replyReceiver: " + replyReceiver);
	        System.out.println("Sender Type: " + senderType);
	        System.out.println("Receiver Type: " + receiverType);

	        return "message/message"; // 메시지 확인 페이지로 이동
	    }
	    // 메시지가 없는 경우, 메시지 홈으로 리다이렉트
	    return "redirect:/messages/message_home";
	}




	
	@PostMapping("/message_post")
	public ResponseEntity<String> saveMessage(@RequestBody Message message, Principal principal) {
	    try {
	        // 보낸 사람 확인
	        String senderName = principal.getName();
	        User sender = userService.getUserByUsername(senderName);
	        Doctor senderDoctor = doctorService.getDoctorByUsername(senderName);

	        if (sender == null && senderDoctor == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인된 사용자 정보를 찾을 수 없습니다.");
	        }

	        // 수신자 확인
	        User receiver = userService.getUserByUsername(message.getReceiver());
	        Doctor receiverDoctor = doctorService.getDoctorByUsername(message.getReceiver());

	        if (receiver == null && receiverDoctor == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("받는 사람 정보가 존재하지 않습니다.");
	        }
	        
	        // 자신에게 메시지 보내기 방지
	        if (senderName.equals(message.getReceiver())) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("self_message"); // 명확한 구분을 위해 "self_message" 반환
	        }

	        // Message 객체에 sender, receiver 저장
	        message.setTitle(message.getTitle());
	        message.setContent(message.getContent());
	        message.setSendDate(LocalDateTime.now());
	        message.setSender(sender != null ? sender.getUsername() : senderDoctor.getUsername()); // sender 저장
	        message.setReceiver(receiver != null ? receiver.getUsername() : receiverDoctor.getUsername()); // receiver 저장

	        // 메시지 저장
	        messageRepo.save(message);

	        return ResponseEntity.ok("메시지가 성공적으로 저장되었습니다.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메시지 저장 중 오류가 발생했습니다.");
	    }
	}
	
	@PostMapping("/message_delete_one/{id}")
	public String deleteMessage(@PathVariable Long id, Principal principal) {
	    try {
	        Optional<Message> optionalMessage = messageRepo.findById(id);
	        if (optionalMessage.isPresent()) {
	            Message message = optionalMessage.get();
	            // 로그인된 사용자 확인
	            String username = principal.getName();

	            // 삭제 권한 확인: 보낸 사람 또는 받은 사람만 삭제 가능
	            if (message.getSender().equals(username) || message.getReceiver().equals(username)) {
	                messageRepo.deleteById(id);
	                return "redirect:/messages/message_home"; // 메시지 홈으로 리다이렉트
	            }
	        }
	        // 권한이 없거나 메시지가 존재하지 않는 경우
	        return "redirect:/messages/message_home?error=not_found";
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "redirect:/messages/message_home?error=server_error";
	    }
	}
	
	@PostMapping("/message_delete")
	public ResponseEntity<String> deleteMessages(@RequestBody Map<String, List<Long>> payload) {
	    List<Long> messageIds = payload.get("messageIds");

	    if (messageIds == null || messageIds.isEmpty()) {
	        return ResponseEntity.badRequest().body("삭제할 메시지가 선택되지 않았습니다.");
	    }

	    messageRepo.deleteAllById(messageIds); // Spring Data JPA 메서드
	    return ResponseEntity.ok("삭제 성공");
	}
}


