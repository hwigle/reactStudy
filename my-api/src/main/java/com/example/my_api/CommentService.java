package com.example.my_api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
	
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository; // 댓글을 달 게시판을 찾기 위함
    private final UserRepository userRepository;   // 댓글 작성자를 찾기 위함	

    
    public CommentService(CommentRepository commentRepository, BoardRepository boardRepository, UserRepository userRepository) {
		this.commentRepository = commentRepository;
		this.boardRepository = boardRepository;
		this.userRepository = userRepository;
    }
    
    /**
     * 특정 게시글의 모든 댓글 조회 (Read)
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션(성능 향상)
    public List<CommentResponseDTO> getCommentsForBoard(Long boardId) {
    	// boardId로 모든 댓글 찾기
    	List<Comment> comments = commentRepository.findByBoardId(boardId);
    	
    	// Entity List -> DTO List로 변환
    	return comments.stream().map(CommentResponseDTO::new).collect(Collectors.toList());
    }
    
    /**
     * 특정 게시글에 새 댓글 작성 (Create)
     */
    @Transactional
    public CommentResponseDTO createComment(Long boardId, CommentRequestDTO requestDTO) {
    	// 현재 로그인한 사용자 정보 조회
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
    	User author = userRepository.findByUsername(username)
    			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); 
    	
    	// 댓글을 달 게시글 조회
    	Board board = boardRepository.findById(boardId)
    			.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    	
    	// 새 Comment 객체 생성 및 정보 설정
    	Comment newComment = new Comment();
    	newComment.setContent(requestDTO.getContent());
    	newComment.setBoard(board);		// 게시글 연결
    	newComment.setAuthor(author);	// 작성자 연결
    	
    	// DB에 저장
    	Comment savedComment = commentRepository.save(newComment);
    	
    	// DTO로 변환하여 반환
    	return new CommentResponseDTO(savedComment);
    }
    
    /**
     * 댓글 삭제 (Delete)
     */
    @Transactional
    public void deleteComment(long commentId) {
    	// 삭제할 댓글 조회
    	Comment comment = commentRepository.findById(commentId)
    			.orElseThrow(() -> new RuntimeException("댓글을 찾을 수 업습니다."));
    	
    	// [권한 검사] 현재 로그인한 사용자와 댓글 작성자가 동일한지 확인
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String authorUsername = comment.getAuthor().getUsername();
        
        if(!currentUsername.equals(authorUsername)) {
        	throw new AccessDeniedException("이 댓글을 삭제할 권한이 없습니다.");
        }
        
        // 삭제
        commentRepository.delete(comment);
    }
}
