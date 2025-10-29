package com.example.my_api;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 1. OncePerRequestFilter : 모든 서블릿 요청에 대해 "단 한 번만" 실행되는 필터
public class JwtTokenFilter extends OncePerRequestFilter {
	
	private final JwtTokenProvider jwtTokenProvider;
	
	// 2. JwtTokenProvider를 주입받음
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    /**
     * 3. [핵심] 실제 필터링(검사) 로직이 수행되는 메서드
     * 		@param request     (HTTP 요청 정보)
     * 		@param response    (HTTP 응답 정보)
     * 		@param filterChain (다음 필터로 요청을 넘기는 통로)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	// 1. "출입증(토큰) 확인"
        //    HTTP 요청(request)에서 토큰을 꺼내옵니다. (아래 헬퍼 메서드 사용)
        String token = resolveToken(request);

        // 2. "출입증(토큰) 검증"
        if (token != null && jwtTokenProvider.validateToken(token)) {
            
            // 3. "신원 확인 완료" (토큰이 유효할 경우)
            Authentication auth = jwtTokenProvider.getAuthentication(token); 
            
            // 4. "보안 금고에 등록"
            //    Spring Security의 "보안 금고"(SecurityContext)를 열어서,
            //    "현재 이 요청을 보낸 사람은 'auth'가 맞습니다"라고 인증 정보를 '저장'.
            //    -> 이 작업이 끝나면, Spring Security는 이 요청을 "인증된 사용자의 요청"으로 간주.
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 5. "다음 단계로 통과"
        //    토큰이 유효하든(4번 실행) 유효하지 않든(4번 건너뜀),
        //    일단 다음 필터(filterChain)로 요청을 그대로 전달(doFilter)합니다.
        //    (만약 토큰이 없어서 4번을 건너뛰었다면, 이 요청은 "익명 사용자"로 취급되어
        //     SecurityConfig의 .anyRequest().authenticated() 규칙에 걸려 차단)
        filterChain.doFilter(request, response);
    }
    
    /**
     * [헬퍼 메서드] HTTP 요청 헤더에서 JWT 토큰을 추출
     * * @param  request (HTTP 요청 정보)
     * 	 @return "Authorization" 헤더에서 찾은 "Bearer " 토큰. 없으면 null.
     */
    private String resolveToken(HttpServletRequest request) {
        // 1. React가 'Authorization'이라는 이름으로 보낸 헤더 값을 꺼냅니다.
        //    (예: "Bearer eyJhbGciOi...")
        String bearerToken = request.getHeader("Authorization"); 
        
        // 2. 헤더 값이 존재하고(null 아님) "Bearer " (띄어쓰기 포함 7글자)로 시작하는지 확인
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            
            // 3. "Bearer " (7글자) 부분을 잘라내고,
            //    그 뒤의 순수 JWT 토큰 문자열(eyJhbGciOi...)만 반환.
            return bearerToken.substring(7); 
        }
        
        // 4. 헤더가 없거나 "Bearer " 방식이 아니면 null을 반환.
        return null;
    }
}
