package com.example.my_api;

import java.time.LocalDateTime;

import lombok.Getter;

/**
 * Spring이 React에게 댓글 목록을 응답(Response)할 때 사용할 DTO
 * LAZY 로딩 문제를 피하기 위해 Entity 대신 사용
 */
@Getter

public class CommentResponseDTO {
    private Long id;
    private String content;
    private String authorUsername; // 작성자 ID 대신 이름만 전송
    private LocalDateTime createdAt;

    // Entity -> DTO 변환을 위한 생성자
    public CommentResponseDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorUsername = (comment.getAuthor() != null) ? comment.getAuthor().getUsername() : "알 수 없음";
        this.createdAt = comment.getCreatedAt();
    }
}
