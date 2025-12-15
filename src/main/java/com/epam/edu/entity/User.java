package com.epam.edu.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "users")
@SecondaryTable(name = "user_details", pkJoinColumns = @PrimaryKeyJoinColumn(name = "users_id"))
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "registration_date")
	private LocalDate registrationDate;

	@Column(name = "name", table = "user_details")
	private String name;

	@Column(name = "surname", table = "user_details")
	private String surname;

	@Column(name = "dob", table = "user_details")
	private LocalDate dateOfBirthday;

	@Column(name = "roles_idroles")
	private Integer roleId;

	@Column(name = "active")
	private Boolean activ;

	@Column(name = "is_author", table = "user_details")
	private Boolean author;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	@Transient
	private UserRole role;

	@PostLoad
	private void loadRole() {
		if (roleId != null) {
			this.role = UserRole.fromId(this.roleId);
		}
	}

	@PrePersist
	@PreUpdate
	private void synchronizeRole() {
		if (role != null) {
			this.roleId = role.getId();
		}
	}

	public User() {

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public LocalDate getDateOfBirthday() {
		return dateOfBirthday;
	}

	public void setDateOfBirthday(LocalDate dateOfBirthday) {
		this.dateOfBirthday = dateOfBirthday;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public Boolean isActiv() {
		return activ;
	}

	public void setActiv(Boolean activ) {
		this.activ = activ;
	}

	public Boolean getAuthor() {
		return author;
	}

	public void setAuthor(Boolean author) {
		this.author = author;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", password=" + password + ", registrationDate="
				+ registrationDate + ", name=" + name + ", surname=" + surname + ", dateOfBirthday=" + dateOfBirthday
				+ ", roleId=" + roleId + ", role=" + role + ", activ=" + activ + ", author=" + author + "]";
	}
}
