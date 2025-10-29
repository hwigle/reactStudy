package com.example.my_api;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 사용자 이름(username)으로 사용자를 찾는 메서드 (JPA Query Method)
    //    Spring Data JPA가 메서드 이름을 분석해서 
    //    "SELECT * FROM users WHERE username = ?" 쿼리를 자동으로 만들어 줍니다.
    Optional<User> findByUsername(String username);

    // 2. (회원가입 시) 사용자 이름이 이미 존재하는지 확인하는 메서드
    boolean existsByUsername(String username);
}