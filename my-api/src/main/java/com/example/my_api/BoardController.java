package com.example.my_api; 

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 		// 1. @Controller + @ResponseBody
						//    이 클래스의 모든 메서드는 HTML 페이지가 아닌,
						//    JSON/데이터 자체를 반환(Response Body)함을 선언
@RequestMapping("/api") // 2. 이 컨트롤러의 모든 메서드에 공통 URL(/api)을 부여
public class BoardController {
	
	// 3. Service 계층을 생성자 주입 방식으로 DI 받음
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // GET (전체)
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/board")
    public Page<BoardResponseDTO> getBoardList(
    		@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) // @PageableDefault : URL 파라미터가 없을 경우 기본값 설정
    		Pageable pageable) {
    	
        return boardService.getAllBoards(pageable);
    }

    // GET (1개)
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/board/{id}")
    public BoardResponseDTO getBoardDetail(@PathVariable("id") Long id) {
        return boardService.getBoardById(id);
    }

    // POST (생성)
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/board")
    public BoardResponseDTO createBoard(@RequestBody Board newBoard) { 
		// @BoardResponseDTO: React가 보낸 HTTP Body의 JSON 데이터(title, content)를
        //               Board 객체로 자동 변환(Deserialization)하여 받음
        return boardService.createBoard(newBoard);
    }

    // PUT (수정)
    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/board/{id}")
    public BoardResponseDTO updateBoard(@PathVariable("id") Long id, @RequestBody Board updatedBoard) {
        return boardService.updateBoard(id, updatedBoard);
    }

    // DELETE (삭제)
    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("/board/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable("id") Long id) {
        boardService.deleteBoard(id);
        
        // 삭제 작업은 성공 시 반환할 데이터(Body)가 없음.
        // '성공적으로 처리했으나 내용은 없음'을 의미하는
        // HTTP 상태 코드 204 No Content를 반환 (RESTful API 표준 방식)
        return ResponseEntity.noContent().build();
    }
}