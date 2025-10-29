package com.example.my_api; 

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity					
@Data 					
@NoArgsConstructor 		
@AllArgsConstructor 	
@Table(name = "users")	// DB 테이블 이름을 'user' 대신 'users'로 지정 (user는 DB 예약어인 경우가 많음)
public class User {
    @Id // 이 필드를 기본키(PK)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID 자동 증가 설정
	private long id;
    
    @Column(nullable = false, unique = true) // null 불가, 중복(unique) 불가
    private String username;

    @Column(nullable = false) // null 불가
    private String password;  // (주의: 실제로는 해시된 비밀번호가 저장됨)
}