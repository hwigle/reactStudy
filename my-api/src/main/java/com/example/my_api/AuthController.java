package com.example.my_api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 데이터 전송을 위한 임시 DTO 클래스
// 별도 파일로 만들어도 됨
record AuthRequest(String username, String password) {}

@RestController
@RequestMapping("/api/auth") // 1. 공통 경로 : /api/auth
public class AuthController {

    private final AuthService authService;
	
	public AuthController(AuthService authService, PasswordEncoder passwordEncoder) {
		this.authService = authService;
	}
	
	
	// 2. [회원가입 API]
	//	  (CORS 설정은 SecurityConfig에서 전역으로 처리,
	//	   BoardController처럼 개별로 추가해야 함)
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/register")
	public ResponseEntity<User> register(@RequestBody AuthRequest request) {
		User user = new User();
		user.setUsername(request.username());
		user.setPassword(request.password());
		
		User registeredUser = authService.register(user);
		
		return ResponseEntity.ok(registeredUser); // 보안상 비번 제외하고 반환하는게 좋음
	}
	
	// 3. [로그인 API]
	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody AuthRequest request) {
		String token = authService.login(request.username(), request.password());
		
		return ResponseEntity.ok(token);
	}

}
