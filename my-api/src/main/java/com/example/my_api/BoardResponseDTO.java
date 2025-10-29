package com.example.my_api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BoardResponseDTO {
	private long id;
	private String title;
	private String content;
	private String authorUsername;
	
	// Entity -> DTO 변환을 위한 생성자
	public BoardResponseDTO(Board board) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.content = board.getContent();
		this.authorUsername = (board.getAuthor() != null) ? board.getAuthor().getUsername() : null;
		
	}

}
