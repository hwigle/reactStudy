package com.example.my_api; 

import org.springframework.data.jpa.repository.JpaRepository;
//JpaRepository<[관리할 Entity 클래스], [Entity의 ID 필드 타입]>
//JpaRepository를 상속받는 인터페이스를 만드는 것만으로,
//Spring Data JPA가 자동으로 이 인터페이스의 구현체를 만듦
//
//이 구현체에는 findAll(), findById(), save(), deleteById() 등
//기본적인 CRUD 메서드들이 이미 모두 포함
public interface BoardRepository extends JpaRepository<Board, Long> {
}