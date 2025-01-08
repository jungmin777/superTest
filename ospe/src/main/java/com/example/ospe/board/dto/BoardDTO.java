package com.example.ospe.board.dto;

import java.util.Date;

// BoardDTO 클래스 정의 (게시판 데이터를 다루기 위한 Data Transfer Object)
public class BoardDTO {

    private Long id; // 게시글 ID
    private String title; // 게시글 제목
    private String content; // 게시글 내용

    // 작성자 (User 또는 Doctor를 받을 수 있도록 Object 타입으로 설정)
    private Object author;

    private Date createdDate; // 생성 날짜
    private Date modifiedDate; // 수정 날짜

    private String createdDateFormatted;

    private String answer; // 답변 필드 추가
    private String answerWriter; // 답변 작성자 이름 추가

    // 기본 생성자
    public BoardDTO() {}

    // 새로운 생성자
    public BoardDTO(Long id, String title, String content, Object author, Date createdDate, Date modifiedDate, String answer, String answerWriter) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.answer = answer;
        this.answerWriter = answerWriter;
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Object getAuthor() {
        return author;
    }
    public void setAuthor(Object author) {
        this.author = author;
    }

    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }
    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreatedDateFormatted() {
        return createdDateFormatted;
    }
    public void setCreatedDateFormatted(String createdDateFormatted) {
        this.createdDateFormatted = createdDateFormatted;
    }

    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswerWriter() {
        return answerWriter;
    }
    public void setAnswerWriter(String answerWriter) {
        this.answerWriter = answerWriter;
    }

    @Override
    public String toString() {
        return "BoardDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author=" + author +
                ", createdDate=" + createdDate +
                ", modifiedDate=" + modifiedDate +
                ", answer='" + answer + '\'' +
                ", answerWriter='" + answerWriter + '\'' +
                '}';
    }
}
