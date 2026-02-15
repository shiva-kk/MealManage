package com.mealManage.domain;

import java.util.List;

public class NotificationRequest {
	
	private List<UserActivationNotification> users;

	/**
	 * @return the users
	 */
	public List<UserActivationNotification> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(List<UserActivationNotification> users) {
		this.users = users;
	}
	
}
