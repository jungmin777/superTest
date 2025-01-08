package com.example.ospe.board.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.ospe.board.dto.BoardDTO;
import com.example.ospe.board.entity.BoardEntity;
import com.example.ospe.board.repository.BoardRepository;
import com.example.ospe.user.dao.DoctorRepository;
import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service // 이 클래스가 Spring의 서비스 클래스임을 나타냄
@Transactional // 이 클래스의 메서드가 트랜잭션으로 처리됨
@RequiredArgsConstructor // Lombok: final 필드를 매개변수로 받는 생성자를 자동 생성
public class BoardService {

    private final BoardRepository boardRepository; // 게시글 데이터를 다루는 Repository
    
    private final DoctorRepository doctorRepository;

    // 모든 게시글을 가져오는 메서드
    public List<BoardDTO> getAllBoards() {
        // 모든 게시글을 생성일 기준 내림차순으로 가져옴
        List<BoardEntity> boardEntities = boardRepository.findAllByOrderByCreatedDateDesc();
        // BoardEntity 리스트를 BoardDTO 리스트로 변환
        return boardEntities.stream()
                .map(this::convertToDTO) // 각 Entity를 DTO로 변환
                .collect(Collectors.toList()); // 변환된 결과를 리스트로 반환
    }

    // ID로 특정 게시글을 가져오는 메서드
    public BoardDTO getBoardById(Long id) {
        // ID로 게시글을 조회
        Optional<BoardEntity> boardEntityOpt = boardRepository.findById(id);
        // Entity를 DTO로 변환하여 반환, 없으면 null 반환
        return boardEntityOpt.map(this::convertToDTO).orElse(null);
    }

    // 새로운 게시글 저장
    public void saveBoard(BoardDTO boardDTO) {
        // 작성자가 없는 경우 예외 처리
        if (boardDTO.getAuthor() == null) {
            throw new IllegalArgumentException("작성자가 없습니다.");
        }
        // DTO를 Entity로 변환
        BoardEntity boardEntity = convertToEntity(boardDTO);
        // 변환된 Entity를 데이터베이스에 저장
        boardRepository.save(boardEntity);
    }

    // 기존 게시글 수정
    public void updateBoard(Long id, BoardDTO boardDTO) {
        // ID로 게시글을 조회
        Optional<BoardEntity> boardEntityOpt = boardRepository.findById(id);
        if (boardEntityOpt.isPresent()) {
            // 조회된 게시글 엔티티를 가져옴
            BoardEntity boardEntity = boardEntityOpt.get();
            // 제목 업데이트
            boardEntity.setTitle(boardDTO.getTitle());
            // 내용 업데이트
            boardEntity.setContent(boardDTO.getContent());
            // 수정된 Entity를 저장 (자동으로 modifiedDate가 갱신됨)
            boardRepository.save(boardEntity);
        }
    }
    
    // 개별 게시글 삭제
 // ID로 개별 게시글 삭제
    public void deleteBoardById(Long id) {
        boardRepository.deleteById(id);  // 주어진 ID에 해당하는 게시글 삭제
    }

    // 선택 게시글 삭제
    public void deleteBoardsByIds(List<Long> ids, Long userId, Long doctorId) {
        List<BoardEntity> boardEntities = boardRepository.findAllById(ids);

        List<Long> deletableIds = boardEntities.stream()
            .filter(board -> isAuthorizedToDelete(board, userId, doctorId))
            .map(BoardEntity::getId)
            .collect(Collectors.toList());

        if (deletableIds.isEmpty()) {
            throw new SecurityException("본인이 작성한 게시글만 삭제할 수 있습니다.");
        }

        boardRepository.deleteAllByIdInBatch(deletableIds);
    }

    public boolean isAuthorizedToDelete(BoardEntity board, Long userId, Long doctorId) {
        Object author = board.getUserAuthor() != null ? board.getUserAuthor() : board.getDoctorAuthor();
        return (author instanceof User && ((User) author).getId().equals(userId)) ||
               (author instanceof Doctor && ((Doctor) author).getId().equals(doctorId));
    }


    
    // User와 Doctor를 구분하여 검색할 수 있도록 BoardService에서 구현을 조정
    public List<BoardDTO> findBoardsByAuthor(String username) {
        List<BoardEntity> userBoards = boardRepository.findByUserAuthorUsername(username);
        List<BoardEntity> doctorBoards = boardRepository.findByDoctorAuthorUsername(username);

        // 두 리스트를 합친 뒤 DTO로 변환
        List<BoardEntity> allBoards = new ArrayList<>();
        allBoards.addAll(userBoards);
        allBoards.addAll(doctorBoards);

        return allBoards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // 추가된 답변 메서드
    public void saveAnswer(Long id, String answer, Principal principal) {
        // 게시글 조회
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));

        // 현재 로그인한 사용자의 username 가져오기
        String loggedInUsername = principal.getName();

        // DoctorRepository를 사용해 Doctor 정보 조회
        List<Doctor> doctors = doctorRepository.findByUsername(loggedInUsername);
        if (!doctors.isEmpty()) {
            Doctor doctor = doctors.get(0);
            board.setAnswerWriter(doctor.getName()); // 의사의 이름을 답변 작성자로 설정
        } else {
            throw new IllegalStateException("로그인된 사용자가 의사가 아닙니다.");
        }

        // 답변 내용 설정 및 저장
        board.setAnswer(answer);
        boardRepository.save(board);
    }



    // BoardEntity를 BoardDTO로 변환
    private BoardDTO convertToDTO(BoardEntity boardEntity) {
        Object author = boardEntity.getAuthor(); // 작성자 정보 가져오기
        Object finalAuthor = null;

        // 작성자 타입 확인 및 캐스팅
        if (author instanceof User || author instanceof Doctor) {
            finalAuthor = author;
        } else {
            throw new IllegalArgumentException("Invalid author type in BoardEntity");
        }

        // 수정일이 없으면 생성일로 대체
        LocalDateTime modifiedDate = boardEntity.getModifiedDate();
        if (modifiedDate == null) {
            modifiedDate = boardEntity.getCreatedDate();
        }

        // LocalDateTime -> Date로 변환
        Date createdDate = Date.from(boardEntity.getCreatedDate().atZone(ZoneId.systemDefault()).toInstant());
        Date finalModifiedDate = Date.from(modifiedDate.atZone(ZoneId.systemDefault()).toInstant());

        // 변환된 DTO 반환
        return new BoardDTO(
                boardEntity.getId(),
                boardEntity.getTitle(),
                boardEntity.getContent(),
                boardEntity.getAuthor(),
                createdDate,
                finalModifiedDate,
                boardEntity.getAnswer(),
                boardEntity.getAnswerWriter() // answerWriter 그대로 전달
        );
    }


    // BoardDTO를 BoardEntity로 변환
    private BoardEntity convertToEntity(BoardDTO boardDTO) {
        // 새 엔티티 객체 생성
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setId(boardDTO.getId()); // ID 설정
        boardEntity.setTitle(boardDTO.getTitle()); // 제목 설정
        boardEntity.setContent(boardDTO.getContent()); // 내용 설정

        // 작성자가 있는 경우 처리
        if (boardDTO.getAuthor() != null) {
            Object author = boardDTO.getAuthor();
            if (author instanceof User || author instanceof Doctor) {
                boardEntity.setAuthor(author); // 작성자 설정
            } else {
                throw new IllegalArgumentException("Invalid author type");
            }
        }
        // 변환된 Entity 반환
        return boardEntity;
    }
}
