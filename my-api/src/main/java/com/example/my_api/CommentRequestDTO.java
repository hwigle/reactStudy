package com.example.my_api;

import lombok.Data;

/**
 * React에서 댓글 생성을 요청할 때 사용할 DTO
 * 댓글 내용(content)만 받음
 * (boardId는 URL에서, author는 Security에서 가져옴)
 */
@Data
public class CommentRequestDTO {
	private String content;
}
