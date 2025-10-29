package com.example.my_api;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider; // 1. JWT 프로바이더 주입
	
	// 1. UserRepository와 PasswordEncoder를 주입받음
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    // [회원가입 로직]
    public User register(User user) {
    	// 1. 사용자 이름 중복 체크
    	if(userRepository.existsByUsername(user.getUsername())) {
    		throw new RuntimeException("이미 사용 중인 사용자 이름입니다.");
    	}
    	
    	// 2. [중요] 비밀번호 암호화(해싱)
    	//    React 에서 받은 평문 비밀번호(예 : "1234")를
    	//    BCrypt 해시로 변경
    	String encodedPassword = passwordEncoder.encode(user.getPassword());
    	user.setPassword(encodedPassword);
    	
    	// 3. DB에 저장
    	return userRepository.save(user);    	
    }
    
    // [로그인 로직]
    public String login(String username, String password) {
    	// 1. DB에서 username으로 사용자 조회
    	User user = userRepository.findByUsername(username)
    			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    	
    	// 2. [중요] 비밀번호 비교
    	//			React에서 받은 평문 비번(password)과
    	//			DB에 저장된 해시 비번(user.getPassword())을 비교
    	if(!passwordEncoder.matches(password, user.getPassword())) {
    		// (보안을 위해 에러 메시지는 구체적으로 쓰지 않는 것이 좋음)
    		throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
    	}
    	
    	// 3. 로그인 성공 시, username으로 JWT 토큰 생성
        return jwtTokenProvider.createToken(user.getUsername());
    }

}
