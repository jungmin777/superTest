package com.example.ospe.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ospe.board.entity.BoardEntity;

// BoardRepository는 기본적으로 JpaRepository를 상속받고 있으므로 CRUD 메서드를 자동으로 사용할 수 있습니다.
@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    // 예를 들어, 특정 조건에 맞는 게시글을 찾고 싶다면, 아래와 같은 메서드를 추가할 수 있습니다.

    // User를 기준으로 검색
    List<BoardEntity> findByUserAuthorUsername(String username);

    // Doctor를 기준으로 검색
    List<BoardEntity> findByDoctorAuthorUsername(String username);

    // 특정 제목을 가진 게시글 찾기
    List<BoardEntity> findByTitleContaining(String title);

    // createdDate를 기준으로 내림차순 정렬하는 쿼리 메서드
    List<BoardEntity> findAllByOrderByCreatedDateDesc();
}
