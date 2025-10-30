package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
	@Autowired
    private SimpMessageSendingOperations messagingTemplate;
	
	/*
	 * 클라이언트가 /app/chat.sendMessage/{roomId} 로 메시지를 보내면 이 메소드가 처리
	 * /topic/room/{roomId} 구독자들에게 메세지를 브로드캐스트
	*/
	@MessageMapping("/chat.sendMessage/{roomId}")
	public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
		
		chatMessage.setRoomId(roomId);
		
		messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
	}
	
	/*
	 * 클라이언트가 /app/chat.addUser로 접속 알림을 보낼 때 처리
	*/
	@MessageMapping("/chat.addUser/{roomId}") 
	public void addUser(@DestinationVariable String roomId, @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
		// WebSocket 세션에 사용자 이름 저장
		headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		headerAccessor.getSessionAttributes().put("roomId", roomId);
		
		chatMessage.setRoomId(roomId);
		
		messagingTemplate.convertAndSend("/topic/room/" + roomId, chatMessage);
	}
}
