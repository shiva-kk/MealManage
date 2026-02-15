package com.mealManage.domain;

import java.util.List;

public class Message {
	
	private List<MessageEnvelope> messageEnvelopes;
	private String messageType;
	/**
	 * @return the messageEnvelopes
	 */
	public List<MessageEnvelope> getMessageEnvelopes() {
		return messageEnvelopes;
	}
	/**
	 * @param messageEnvelopes the messageEnvelopes to set
	 */
	public void setMessageEnvelopes(List<MessageEnvelope> messageEnvelopes) {
		this.messageEnvelopes = messageEnvelopes;
	}
	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}
	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}
