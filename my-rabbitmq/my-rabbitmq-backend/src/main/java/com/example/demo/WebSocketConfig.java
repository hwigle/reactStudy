package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// STOMP 메시지를 RabbitMQ 브로커로 라우팅(릴레이)
		registry.enableStompBrokerRelay("/topic") // "topic"으로 시작하는 목적지
				.setRelayHost("localhost")		  // RabbitMQ 호스트
				.setRelayPort(61613) 			  // RabbitMQ STOMP 포트
				.setClientLogin("guest")
				.setClientPasscode("guest");
		
		// 클라이언트가 서버로 메시지를 보낼 때 사용할 접두사
		registry.setApplicationDestinationPrefixes("/app");
				
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 클라이언트가 WebSocket에 연결할 엔드포인트
		// /ws 엔드포인트를 열고, 모든 도메인에서 접속 허용(CORS)
		registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:8080", "http://127.0.0.1:8080").withSockJS();
		// withSockJS()는 WebSocket을 지원하지 않는 브라우저를 위한 대체 옵션
	}

}
