package com.example.ospe.board.entity;

import java.time.LocalDateTime;

import com.example.ospe.user.dto.Doctor;
import com.example.ospe.user.dto.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "board_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = true) // 환자와 연결
    private User userAuthor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = true) // 의사와 연결
    private Doctor doctorAuthor;

    @Column(nullable = true)
    private String answer; // 답변 컬럼 추가
    
    @Column(name = "answer_writer", nullable = true)
    private String answerWriter; // 답변 작성자 이름

    @Column(nullable = false)
    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }

    // 작성자 가져오기 (User 또는 Doctor 반환)
    public Object getAuthor() {
        if (userAuthor != null) {
            return userAuthor;
        } else if (doctorAuthor != null) {
            return doctorAuthor;
        }
        return null; // 작성자가 없는 경우 null 반환
    }

    // 작성자 설정하기 (User 또는 Doctor를 받아 설정)
    public void setAuthor(Object author) {
        if (author instanceof User) {
            this.userAuthor = (User) author;
            this.doctorAuthor = null; // 기존 의사 정보 제거
        } else if (author instanceof Doctor) {
            this.doctorAuthor = (Doctor) author;
            this.userAuthor = null; // 기존 사용자 정보 제거
        } else {
            throw new IllegalArgumentException("Invalid author type");
        }
    }
    
    public String getAnswerWriter() {
        return answerWriter;
    }

    public void setAnswerWriter(String answerWriter) {
        this.answerWriter = answerWriter;
    }
}