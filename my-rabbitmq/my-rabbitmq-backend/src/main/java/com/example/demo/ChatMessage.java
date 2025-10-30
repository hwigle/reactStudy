package com.example.demo;

import lombok.Data;

@Data
public class ChatMessage {
	private String sender;
	private String content;
	private MessageType type;
	private String roomId;
	
	public enum MessageType {
		CHAT, JOIN, LEAVE
	}

}
