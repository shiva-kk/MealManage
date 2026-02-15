package com.mealManage.mealmodel.user;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "SuperAdminUser_v2")
/**This entity having the user name properties and extending to Users entity**/
public class SuperAdminUser extends User implements Serializable{
	
	private static final long serialVersionUID = 6034024176557072678L;
	
	@Column(unique=true, nullable = false)
	private String username;

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
