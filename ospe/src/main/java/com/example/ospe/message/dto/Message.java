package com.example.ospe.message.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message_table")
@SequenceGenerator(
        name = "MessageSeq",
        sequenceName = "MessageSeq",
        allocationSize = 1,
        initialValue = 1
)
public class Message {
	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "MessageSeq"
	)
	private Long id;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = false)
	private LocalDateTime sendDate;
	
    @Transient
    private String sendDateFormatted;
	
	@Column(nullable = false)
	private String receiver;

	@Column(nullable = false)
    private String sender;

}











