package com.example.my_api;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component // 1. Spring Bean으로 등록
public class JwtTokenProvider {
	
	// 2. [중요] JWT 서명에 사용할 비밀키(절대 노출되면 안 됨)
	//	   이 키는 application.properties에 따로 보관
	private final Key key;
	
	// 3. 토큰 유효 시간(예 : 1시간)
	private final long validityInMilliseconds = 3600000; // 1h
	
	// 4. application.properties에서 비밀키 값을 읽어봐 Key 객체로 변환
	public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
		// 주입받은 비밀 문자열(secretKey)을 byte 배열로 변환
        // jjwt 라이브러리의 Keys 유틸리티를 사용하여, HMAC-SHA 알고리즘(HS256)에 적합한 암호화 'Key' 객체를 생성
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
	}
	
	// 5. [토근 생성] 사용자 이름(usernmae)으로 토큰을 생성
	public String createToken(String username) {
		// 'Claims'는 토큰에 담을 정보(Payload)의 단위(Key-Value 쌍)
		Claims claims = Jwts.claims()
				// 'Subject'(주제)로 사용자 이름(username)을 지정
	            // (즉, 이 토큰의 '주인'이 누구인지 명시)
				.setSubject(username);
				// (필요시 claims.put("roles", "USER"); 같은 추가 정보 저장 가능)
		
		Date now = new Date(); // 현재 시간
		Date validity = new Date(now.getTime() + validityInMilliseconds); // 만료 시간
		
		return Jwts.builder()
                .setClaims(claims)      	// 1) 정보(Payload) 설정: 위에서 만든 claims 객체
                .setIssuedAt(now)       	// 2) 토큰 발행 시간(iat) 설정: 현재 시간
                .setExpiration(validity) 	// 3) 토큰 만료 시간(exp) 설정: 1시간 뒤
                .signWith(key, SignatureAlgorithm.HS256) // 4) 서명(Signature) 설정
                .compact(); 				// 5) 위 내용을 조합하여 실제 JWT 문자열(String)으로 압축(생성)
	}
	
	// 6. [토큰 검증] 토큰이 유효한지 + 만료되었는지 확인
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()		// 토큰을 해석(parse)하는 빌더를 생성
				.setSigningKey(key)		// 우리가 보관 중인 'key'로 서명 검증
				.build()				//  해석기(parser) 생성
				.parseClaimsJws(token); // 실제 'token'을 해석
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	// 7. [정보 추출] 유효한 토큰에서 'Payload'에 저장된 사용자 이름(Subject) 추출
	public String getUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build()  // 1) 토큰 해석
					.parseClaimsJws(token)	// 2) 검증 및 파싱
					.getBody()				// 3) 'Payload'(Claims) 부분
					.getSubject();			// 4) Payload에 저장했던 'Subject'(username) 반환
	}
	
	/**
     * 8. [인증 정보 생성]
     * 토큰이 유효할 때, 토큰에 담긴 정보(username)를 기반으로
     * Spring Security의 'Authentication' 객체(인증 정보)를 생성합니다.
     *
     * @param token 검증된 JWT 토큰
     * @return Spring Security가 이해할 수 있는 Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 사용자 이름(username)을 가져옴
        String username = this.getUsername(token);

        // 2. [중요] 'UserDetails' 객체 생성
        //    (원래는 DB에서 사용자 Role을 조회해야 하지만, 지금은 간단히 "USER" 권한 부여)
        //    비밀번호는 이미 검증되었으므로 ""(빈칸) 처리
        UserDetails userDetails = User.builder()
                .username(username)
                .password("") // (중요) 비밀번호는 비워둠
                .authorities(new ArrayList<>()) // (권한 목록, 지금은 비워둠)
                .build();

        // 3. 'Authentication' 객체(UsernamePasswordAuthenticationToken)를 생성하여 반환
        return new UsernamePasswordAuthenticationToken(
            userDetails, // (주체, Principal) - 사용자 정보
            "",          // (자격 증명, Credentials) - 비밀번호 (비워둠)
            userDetails.getAuthorities() // (권한, Authorities)
        );
    }

}
