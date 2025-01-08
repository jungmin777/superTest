package com.example.ospe.message.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ospe.message.dto.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	Message save(Message message);
	
	@Query("SELECT m FROM Message m WHERE m.sender = :sender")
	List<Message> findBySender(@Param("sender") String sender);

	@Query("SELECT m FROM Message m WHERE m.receiver = :receiver")
	List<Message> findByReceiver(@Param("receiver") String receiver);
    
    // 페이지네이션 레포지토리
    Page<Message> findAllBySender(String sender, Pageable pageable);
    Page<Message> findAllByReceiver(String receiver, Pageable pageable);
    
    // 제목 또는 내용에서 키워드 검색
    List<Message> findByTitleContainingOrContentContaining(String titleKeyword, String contentKeyword);
    // 보낸 사람 또는 받은 사람에서 검색
    List<Message> findBySenderOrReceiver(String sender, String receiver);

}
