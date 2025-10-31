package com.example.my_api;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글(Board)에 달린 모든 댓글을 찾는 쿼리 메서드
    List<Comment> findByBoardId(Long boardId);
}
