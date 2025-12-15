package com.epam.edu.entity;

public enum UserRole {
	GUEST(3, "GUEST"), USER(2, "USER"), ADMIN(1, "ADMIN");

	private final int id;
	private final String name;

	UserRole(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public static UserRole fromId(int id) {
		for (UserRole role : values()) {
			if (role.id == id)
				return role;
		}
		throw new IllegalArgumentException("Unknown role id: " + id);
	}
}