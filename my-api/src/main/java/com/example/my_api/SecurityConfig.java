package com.example.my_api;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration		// 1.이 클래스가 Spring '설정' 클래스임을 알림
@EnableWebSecurity	// 2. SpringSecurity 활성화
public class SecurityConfig {
	
	// 2. JwtTokenProvider를 주입받음
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
	// 3. 비밀번호 암호화(해싱)를 위한 PasswordEncoder Bean 등록
	//    AuthService에서 이 객체를 주입받아 비밀번호를 암호화하고 비교하는 데 사용
	@Bean // 👈 이 메서드가 반환하는 객체(Bean)를 Spring 관리
	public PasswordEncoder passwordEncoder() {
		// BCrypt는 강력한 해시 알고리즘 중 하나
		return new BCryptPasswordEncoder();
	}
	
	// 4. Spring Security의 메인 보안 설정을 정의
	//    HTTP 요청을 어떻게 필터링하고 처리할지 설정
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		//// http 객체를 받아 설정을 시작합니다
		http
			// --- [CORS 설정 추가] ---
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        // --- [CORS 설정 끝] ---
			// 5. CSRF(Cross-Site Request Forgery) 보호 비활성화
			//	  (JWT/토큰 기반 인증에서는 세션을 사용하지 않으므로 CSRF 보호가 불필요)
			.csrf(csrf -> csrf.disable()) // (람다 사용) "csrf 설정 담당자"를 받아서 "disable" 시킴
			// --- [H2 콘솔 프레임 허용] ---
            // H2 콘솔은 <frame>을 사용하므로, X-Frame-Options 헤더를 비활성화해야 함
            // (보안상 동일 출처(sameOrigin)의 프레임만 허용하는 것이 더 좋음)
			// (람다 사용) "headers 설정 담당자"를 받아서 "frameOptions"를 "sameOrigin"으로 설정
            .headers(headers -> 
                headers.frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
			// 6. 세션 정책 설정: STATELESS (상태 비저장)
            // (람다 사용) "session 설정 담당자"를 받아서 "정책"을 "STATELESS"로 설정
            // (서버가 세션이나 쿠키 대신 JWT 토큰을 사용하도록 함)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// 7. HTTP 요청 경로별 접근 권한 설정
            .authorizeHttpRequests(authz -> authz
                    // /api/auth/** (로그인/회원가입), /h2-console/** 는 누구나 허용
                    .requestMatchers("/h2-console/**", "/api/auth/**").permitAll() 
                    
                    // "읽기" 전용 (GET) 요청은 누구나 허용
                    // - GET /api/board (게시글 목록)
                    // - GET /api/board/{id} (게시글 상세)
                    // - GET /api/board/{boardId}/comments (댓글 목록)
                    .requestMatchers(HttpMethod.GET, "/api/board", "/api/board/**").permitAll() 
                    
                    // 그 외 "모든" 요청은 인증(로그인)이 필요
                    // (즉, POST, PUT, DELETE 등 "쓰기" 요청은 모두 인증 필요)
                    .anyRequest().authenticated() 
                )
			// --- 👇 [필터 등록 (핵심)] ---
            // 3. 우리가 만든 'JwtTokenFilter'를 
            //    Spring Security의 'UsernamePasswordAuthenticationFilter' (로그인 처리 필터)
            //    *이전에* 배치하라고 명령합니다.
            .addFilterBefore(
                new JwtTokenFilter(jwtTokenProvider), // (경비원 필터)
                UsernamePasswordAuthenticationFilter.class // (로그인 필터)
            );;
		
		// 설정이 완료된 HttpSecurity 객체를 빌드하여 반환
		return http.build();
	}
	
	// ---[CORS 상세 설정 Bean 추가] ---
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // [중요] React 앱의 주소(Origin)를 명시적으로 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
        // 허용할 HTTP 메서드 (GET, POST, PUT, DELETE 등)
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
        // 허용할 HTTP 헤더 (Authorization 헤더 포함)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // (선택) 자격 증명(쿠키 등) 허용 여부 - 지금은 JWT 토큰 방식이라 false로 둬도 무방
        // configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위에서 정의한 CORS 설정을 적용합니다.
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
