package com.epam.edu.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "news")
public class News {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "news_group_id", nullable = false)
	private NewsGroup newsGroup;

	@Column(name = "title")
	private String title;

	@Column(name = "brief", length = 1000)
	private String brief;

	@Column(name = "content_path")
	private String contentPath;

	@Column(name = "publish_date")
	private LocalDateTime publishingDateTime;

	@Column(name = "create_date", nullable = false, updatable = false)
	private LocalDateTime createDateTime;

	@Column(name = "updated_date")
	private LocalDateTime updateDateTime;

	@ManyToMany
	@JoinTable(name = "authors", joinColumns = @JoinColumn(name = "news_id"), inverseJoinColumns = @JoinColumn(name = "users_id"))
	private List<User> authors = new ArrayList<User>();

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User publisher;

	@Column(name = "is_active")
	private boolean activ;

	@Transient
	private String content;

	@OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Comment> commentaries;

	@PrePersist
	protected void onCreate() {
		this.createDateTime = LocalDateTime.now();
		this.updateDateTime = this.createDateTime;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updateDateTime = LocalDateTime.now();
	}

	public News() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NewsGroup getNewsGroup() {
		return newsGroup;
	}

	public void setNewsGroup(NewsGroup newsGroup) {
		this.newsGroup = newsGroup;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getContentPath() {
		return contentPath;
	}

	public void setContentPath(String contentPath) {
		this.contentPath = contentPath;
	}

	public LocalDateTime getPublishingDateTime() {
		return publishingDateTime;
	}

	public void setPublishingDateTime(LocalDateTime publishingDateTime) {
		this.publishingDateTime = publishingDateTime;
	}

	public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(LocalDateTime createDateTime) {
		this.createDateTime = createDateTime;
	}

	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public List<User> getAuthors() {
		return authors;
	}

	public void setAuthors(List<User> authors) {
		this.authors = authors;
	}

	public User getPublisher() {
		return publisher;
	}

	public void setPublisher(User publisher) {
		this.publisher = publisher;
	}

	public boolean isActiv() {
		return activ;
	}

	public void setActiv(boolean activ) {
		this.activ = activ;
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
		News other = (News) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", group=" + newsGroup + ", title=" + title + ", brief=" + brief + ", contentPath="
				+ contentPath + ", publishingDateTime=" + publishingDateTime + ", createDateTime=" + createDateTime
				+ ", updateDateTime=" + updateDateTime + ", publisher=" + publisher + ", newsStatusId=" + activ
				+ ", content=" + content + "]";
	}
}
