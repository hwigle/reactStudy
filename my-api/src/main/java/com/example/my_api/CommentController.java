package com.example.my_api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 댓글(Comment) 관련 API를 제공하는 컨트롤러
 * @RestController : 이 클래스의 모든 메서드는 JSON/데이터를 반환
 */
@Controller
@RequestMapping("/api") // 공통 경로 /api
public class CommentController {
	
	private final CommentService commentService;
	
	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}
	
    /**
     * (Read) 특정 게시글의 모든 댓글 조회
     * API: GET /api/board/{boardId}/comments
     */
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/board/{boardId}/comments")
	public ResponseEntity<List<CommentResponseDTO>> getComments(@PathVariable("boardId") Long boardId) {
		List<CommentResponseDTO> comments = commentService.getCommentsForBoard(boardId);
		return ResponseEntity.ok(comments);
	}
	
	 /**
     * (Create) 특정 게시글에 새 댓글 작성
     * API: POST /api/board/{boardId}/comments
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/board/{boardId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
    		@PathVariable("boardId") Long boardId,  // URL에서 게시글 ID 추출
            @RequestBody CommentRequestDTO requestDTO // JSON 본문에서 댓글 내용 추출
    ) {
        CommentResponseDTO createdComment = commentService.createComment(boardId, requestDTO);
         return ResponseEntity.ok(createdComment);
    }

    /**
     * (Delete) 특정 댓글 삭제
     * API: DELETE /api/comments/{commentId}
     */
    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        // 삭제 성공 시 204 No Content 응답
        return ResponseEntity.noContent().build();
    }

}
