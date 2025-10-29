package com.example.my_api; 

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity					// 이 클래스를 JPA Entity로 선언(DB 테이블과 매핑됨)
@Data 					// Lombok : Getter/Setter 등을 자동으로 만듦
@NoArgsConstructor 		// Lombok : 기본 생성자
@AllArgsConstructor 	// Lombok : 모든 필드를 받는 생성자
public class Board {
    @Id // 이 필드를 기본키(PK)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 자동 증가 설정
	private long id;
    
	private String title;
	
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY)	// 다대일 관계(게시글 N : 작성자 1)
										// FetchType.LAZY는 게시글을 조회할 때 작성자 정보까지 바로 불러오지 않고,
										// 필요할 때 불러오도록 해서 성능을 최적화
	@JoinColumn(name = "user_id")		// 외래키(FK) 컬럼 이름을 'user_id'로 지정
	private User author; // User 엔티티 타입
}