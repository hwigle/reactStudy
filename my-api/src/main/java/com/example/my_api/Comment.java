package com.example.my_api;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 이 클래스가 JPA Entity임을 선언 (DB의 테이블과 1:1로 매핑)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments") // DB 테이블 이름
public class Comment {
	
	@Id // 기본키
	@GeneratedValue(strategy = GenerationType.IDENTITY) // PK 값 자동 생성
	private Long id;
	
	@Column(nullable = false)
	private String content;	// 댓글 내용
	
	@ManyToOne // [연관 관계 1 : 댓글(N) -> 게시글(1)]
	@JoinColumn(name = "board_id", nullable = false) // 외래키 
	private Board board; 	// 댓글이 달린 게시글
	
	@ManyToOne // [연관 관계 2 : 댓글(N) -> 작성자(1)]
	@JoinColumn(name = "user_id", nullable = false)
	private User author;	// 댓글을 쓴 작성자
	
	@CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
