package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketEventListener {
	
	// 메세지를 /topic/general 토픽으로 전송(브로드캐스트)하기 위해 필요
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	/*
	 * WebSocket 연결이 끊어졌을 때(퇴장) 실행되는 리스너
	*/
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		
		// 연결이 끊어진 세션의 헤더 정보
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		
		// 세션에서 username과 roomId를 가져옴 (addUser에서 저장했던 값)
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
		
		if(username != null && roomId != null) {
			log.info("사용자 퇴장 : {}, 방 ID : {}", username, roomId);
			
			// ChatMessage 객체 생성(LEAVE 타입)
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setType(ChatMessage.MessageType.LEAVE);
			chatMessage.setSender(username);
			chatMessage.setRoomId(roomId);
			
			// /topic/room/{roomId} 토픽으로 퇴장 메시지를 브로드캐스트
            messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
		}
	}
}
