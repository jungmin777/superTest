package com.example.ospe.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ospe.board.dto.BoardDTO;
import com.example.ospe.board.service.BoardService;
import com.example.ospe.config.CustomUserDetails;
import com.example.ospe.reservation.dto.Headerlogin;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;
import com.example.ospe.user.service.DoctorService;
import com.example.ospe.user.service.UserService;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserService userService;

    @Autowired
    private DoctorService doctorService;
    
    @Autowired
	Headerlogin keep; // 로그인 유지 재사용 Headerlogin 클래스

	@ModelAttribute //모든 매핑에 추가할 코드
    public void addAttributes(Model model, Principal principal) {
        keep.headerlogin(model, principal); //로그인 유지 
    }

    // 로그인된 사용자의 ID 가져오기
    private Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            List<User> users = userService.findByUsername(username);
            if (!users.isEmpty()) {
                return users.get(0).getId();
            }
        }
        return null;
    }

    private Long getLoggedInDoctorId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            Optional<Doctor> doctorOptional = doctorService.findByUsername(username).stream().findFirst();
            if (doctorOptional.isPresent()) {
                return doctorOptional.get().getId();
            }
        }
        return null;
    }

    @GetMapping("/board_home")
    public String boardHome(
        @AuthenticationPrincipal CustomUserDetails userDetails, 
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(value = "searchselect", required = false) String searchselect,
        @RequestParam(value = "searchinput", required = false) String searchinput,
        Model model, Principal principal
    ) {
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("username", principal != null ? principal.getName() : null);

        // 현재 로그인 사용자가 의사인지 여부 추가
        boolean isDoctor = getLoggedInDoctorId() != null;
        model.addAttribute("isDoctor", isDoctor);

        int pageSize = 10; // 한 페이지에 표시할 게시글 개수
        int paginationSize = 10; // 페이지 번호 최대 표시 개수

        // 모든 게시글 가져오기
        List<BoardDTO> userBoards = boardService.getAllBoards();

        // 검색 조건 적용
        if (searchselect != null && !searchselect.isEmpty() && searchinput != null && !searchinput.isEmpty()) {
            switch (searchselect) {
                case "제목/내용":
                    userBoards = userBoards.stream()
                        .filter(board -> board.getTitle().contains(searchinput) || board.getContent().contains(searchinput))
                        .collect(Collectors.toList());
                    break;
                case "작성자":
                    userBoards = userBoards.stream()
                        .filter(board -> {
                            Object author = board.getAuthor();
                            if (author instanceof User) {
                                return ((User) author).getName().contains(searchinput);
                            } else if (author instanceof Doctor) {
                                return ((Doctor) author).getName().contains(searchinput);
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
                    break;
                default:
                    // 검색 조건이 없거나 잘못된 경우 그대로 진행
            }
        }

        // 전체 게시글 개수
        int totalBoards = userBoards.size();
        int totalPages = (int) Math.ceil((double) totalBoards / pageSize);

        // 페이지네이션 범위 계산
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalBoards);

        // 날짜 포맷 조건 추가
        LocalDateTime now = LocalDateTime.now();
        userBoards.forEach(board -> {
            if (board.getCreatedDate() != null) {
                LocalDateTime createdDate = board.getCreatedDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

                if (createdDate.toLocalDate().isEqual(now.toLocalDate())) {
                    // 당일 작성된 글
                    board.setCreatedDateFormatted(createdDate.format(DateTimeFormatter.ofPattern("HH:mm")));
                } else if (createdDate.getYear() == now.getYear()) {
                    // 올해 작성된 글
                    board.setCreatedDateFormatted(createdDate.format(DateTimeFormatter.ofPattern("MM/dd")));
                } else {
                    // 올해 이전에 작성된 글
                    board.setCreatedDateFormatted(createdDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
                }
            }
        });

        // 현재 페이지에 해당하는 게시글 추출
        List<BoardDTO> paginatedBoards = userBoards.subList(startIndex, endIndex);

        // 페이지네이션 범위 계산
        int currentRangeStart = ((page - 1) / paginationSize) * paginationSize + 1;
        int currentRangeEnd = Math.min(currentRangeStart + paginationSize - 1, totalPages);

        // 모델에 데이터 추가
        model.addAttribute("boards", paginatedBoards);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", currentRangeStart);
        model.addAttribute("endPage", currentRangeEnd);
        model.addAttribute("searchselect", searchselect);
        model.addAttribute("searchinput", searchinput);

        return "board/board_home";
    }


    // 게시글 작성 페이지
    @GetMapping("/board_post")
    public String showBoardPostForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("username", principal != null ? principal.getName() : null);

        BoardDTO boardDTO = new BoardDTO();
        String username = userDetails.getUsername();
        Object author = userService.getUserByUsername(username);
        if (author == null) {
            author = doctorService.getDoctorByUsername(username);
        }
        boardDTO.setAuthor(author);
        model.addAttribute("boardPost", boardDTO);
        return "board/board_post";
    }

    // 게시글 저장 처리
    @PostMapping("/board_post")
    public String saveBoardPost(@ModelAttribute BoardDTO boardDTO) {
        Long userId = getLoggedInUserId();
        Long doctorId = getLoggedInDoctorId();

        if (userId != null) {
            Optional<User> userOptional = userService.findById(userId);
            userOptional.ifPresent(boardDTO::setAuthor);
        } else if (doctorId != null) {
            Optional<Doctor> doctorOptional = doctorService.findById(doctorId);
            doctorOptional.ifPresent(boardDTO::setAuthor);
        } else {
            return "redirect:/login";
        }

        boardService.saveBoard(boardDTO);
        return "redirect:/board_home";
    }
    
    @GetMapping("/post/{id}")
    public String getPost(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("loggedIn", principal != null);
        model.addAttribute("username", principal != null ? principal.getName() : null);

        // 현재 로그인한 사용자 ID 추가
        Long loggedInUserId = getLoggedInUserId();
        model.addAttribute("loggedInUserId", loggedInUserId);

        // 로그인 사용자가 환자인지 여부를 추가
        boolean isPatient = getLoggedInDoctorId() == null;
        model.addAttribute("isPatient", isPatient);

        // 게시글 가져오기
        BoardDTO board = boardService.getBoardById(id);
        model.addAttribute("board", board);

        Object author = board.getAuthor();
        if (author instanceof User) {
            model.addAttribute("authorName", ((User) author).getName());
        } else if (author instanceof Doctor) {
            model.addAttribute("authorName", ((Doctor) author).getName());
        }

        // 답변 데이터 추가
        String answer = board.getAnswer();
        model.addAttribute("answer", answer);

        return "board/post";
    }


    @PostMapping("/post/{id}/answer")
    public String saveAnswer(@PathVariable Long id, @RequestParam String answer, Principal principal) {
        boardService.saveAnswer(id, answer, principal);
        return "redirect:/post/" + id;
    }


    // 단일 삭제 처리
    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        boardService.deleteBoardById(id);  // 게시글 삭제
        return "redirect:/board_home";  // 삭제 후 홈으로 이동
    }

    // 선택 삭제 처리
    @PostMapping("/deleteBoards")
    @ResponseBody
    public ResponseEntity<String> deleteSelectedBoards(@RequestBody Map<String, List<Long>> requestData) {
        List<Long> ids = requestData.get("ids");
        Long userId = getLoggedInUserId();
        Long doctorId = getLoggedInDoctorId();

        if (userId == null && doctorId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        try {
            boardService.deleteBoardsByIds(ids, userId, doctorId);
            return ResponseEntity.ok("선택한 게시글이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

}
