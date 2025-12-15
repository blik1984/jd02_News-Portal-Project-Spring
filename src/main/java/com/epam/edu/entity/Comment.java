package com.epam.edu.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "commentaries")
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "news_id", nullable = false)
	private News news;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "users_id")
	private User user;

	@Column(name = "content")
	private String text;

	@Column(name = "created_date")
	private LocalDateTime createdAt;

	@Column(name = "updated_date")
	private LocalDateTime updatedAt;

	@Column(name = "is_activ")
	private boolean activ;

	@Transient
	private boolean editable;

	public Comment() {
	}

	@Transient
	public boolean isEditable(User currentUser) {
		if (currentUser == null || user == null) {
			return false; 
		}
		boolean isAuthor = currentUser.getId().equals(user.getId());
		boolean withinTimeLimit = createdAt != null && createdAt.isAfter(LocalDateTime.now().minusMinutes(30));
		return isAuthor && withinTimeLimit;
	}

	@Transient
	public String getUserName() {
		return user != null ? user.getName() : "Anonymous";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isActiv() {
		return activ;
	}

	public void setActiv(boolean activ) {
		this.activ = activ;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Comment comment = (Comment) o;
		return Objects.equals(id, comment.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Comment{" + "id=" + id + ", text='" + text + '\'' + ", createdAt=" + createdAt + ", updatedAt="
				+ updatedAt + ", activ=" + activ + '}';
	}
}