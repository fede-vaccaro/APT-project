package com.unifiprojects.app.appichetto.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity(name = "users")
public class User {

	@Version
	private int version;

	@Id
	@GeneratedValue
	private Long id;

	public Long getId() {
		return id;
	}

	private String username;
	private String password;

	public User() {

	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else {
			if (!username.equals(other.username))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.username;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
