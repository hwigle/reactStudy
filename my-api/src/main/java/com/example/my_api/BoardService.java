package com.example.my_api;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service // Spring이 이 클래스를 'Service' Bean으로 등록
public class BoardService {
	
	// 'final' : 불변성. 참조가 바뀌지 않음
	// -> BoardRepository가 반드시 '생서자 주입'을 통해 초기화되어야 함을 강제
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 생성자 주입(Dependency Injection)
    // Spring이 자동으로 BoardRepository의 구현체(Bean)를 찾아서
    // 이 생성자의 파라미터로 '주입'
    public BoardService(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * 전체 목록 '페이징' 조회 (Read All with Pagination)
     * @param pageable 요청된 페이지 정보 (페이지 번호, 페이지 크기, 정렬 순서)
     * @return 해당 페이지의 게시글 DTO 리스트와 페이지 정보를 담은 Page 객체
     */
    public Page<BoardResponseDTO> getAllBoards(Pageable pageable) { 
    	// Pageable 객체를 사용하여 DB에서 Board Entity 페이지 조회
    	Page<Board> boardPage = boardRepository.findAll(pageable);
    	
    	// 조회된 Board Entity 페이지를 BoardResponseDTO 페이지로 변환
    	// .map(엔티티 -> DTO 변환 함수) 사용
    	return boardPage.map(BoardResponseDTO::new);
    	
    }

    // ID로 1개 조회 (Read One)
    public BoardResponseDTO getBoardById(Long id) {
    	Board board = boardRepository.findById(id).orElse(null); // DB에서 Board 조회
        return (board != null) ? new BoardResponseDTO(board) : null; // Board가 있으면 DTO로 변환, 없으면 null 반환 
    }

    // 새 게시글 생성 (Create)
    public BoardResponseDTO createBoard(Board newBoard) {
    	// JpaRepository의 save() 메서드는
        //   - 객체의 ID가 null이거나 0이면 (DB에 없으면) -> INSERT 쿼리 실행
        //   - 객체의 ID가 존재하면 (DB에 있으면) -> UPDATE 쿼리 실행
        // 지금은 id가 0L(또는 null)인 객체가 오므로 INSERT가 실행
    	// 현재 로그인한 사용자의 Authentication 객체 가져옴
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	// Authentication 객체에서 사용자 이름(username) 추출
    	String username = authentication.getName();
    	
    	// 사용자 이름으로 User 엔티티를 DB에서 조회
    	User currentUser = userRepository.findByUsername(username)
    			.orElseThrow(() -> new RuntimeException("현재 로그인된 사용자를 찾을 수 없습니다."));
    			
    	// 새 게시글(newBoard)의 작성자(author) 필드에 조회한 User 엔티티를 설정
    	newBoard.setAuthor(currentUser);
    	
        Board savedBoard = boardRepository.save(newBoard);
    	
        return new BoardResponseDTO(savedBoard);
    }
    
    // 게시글 수정 (Update)
    public BoardResponseDTO updateBoard(Long id, Board updatedBoard) {
    	// 1. DB에서 ID로 기존 데이터를 먼저 조회 (영속성 컨텍스트에 올림)
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID " + id + " not found"));
        
        checkAuthorization(existingBoard); // 현재 사용자가 작성자인지 확인

        // 2. 기존 객체의 필드 값만 React에서 받은 새 값으로 변경
        existingBoard.setTitle(updatedBoard.getTitle());
        existingBoard.setContent(updatedBoard.getContent());
        
        // 3. 변경된 existingBoard를 다시 저장(save)
        //	  (JPA가 id가 존재하는 것을 보고 UPDATE 쿼리를 실행)
        Board savedBoard = boardRepository.save(existingBoard); // 수정된 Entity 저장
        return new BoardResponseDTO(savedBoard); // 저장된 Entity를 DTO로 변환하여 반환
    }

    // 게시글 삭제 (Delete)
    public void deleteBoard(Long id) {
    	// 1. 삭제 전 게시글 조회 (작성자 확인 위해)
        Board boardToDelete = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID " + id + " not found"));

        // 2. [핵심] 권한 검사 로직 추가
        checkAuthorization(boardToDelete); // 현재 사용자가 작성자인지 확인

        // 3. (권한 통과 시) 삭제 실행
        boardRepository.deleteById(id);
    }
    
    // --- [권한 검사 헬퍼 메서드 추가] ---
    /**
     * 현재 로그인한 사용자가 주어진 게시글의 작성자인지 확인
     * 작성자가 아니면 AccessDeniedException을 발생
     * @param board 검사할 게시글 Entity
     */
    private void checkAuthorization(Board board) {
        // 1. 현재 로그인한 사용자의 Authentication 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
             throw new AccessDeniedException("로그인이 필요합니다."); // 비로그인 접근 차단
        }

        // 2. 현재 로그인한 사용자 이름 가져오기
        String currentUsername = authentication.getName();

        // 3. 게시글 작성자 이름 가져오기 (author 필드가 null일 수 있으므로 주의)
        String authorUsername = (board.getAuthor() != null) ? board.getAuthor().getUsername() : null;

        // 4. 두 이름이 다르면 예외 발생 (403 Forbidden 에러로 응답됨)
        if (!currentUsername.equals(authorUsername)) {
            throw new AccessDeniedException("이 게시글을 수정/삭제할 권한이 없습니다.");
        }
    }
}