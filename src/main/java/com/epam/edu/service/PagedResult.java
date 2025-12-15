package com.epam.edu.service;

import java.util.List;
import java.util.Objects;

public class PagedResult<T> {
	private List<T> content;
	private int currentPage;
	private int pageSize;
	private long totalElements;
	private int totalPages;

	public PagedResult(List<T> content, int currentPage, int pageSize, long totalElements) {
		this.content = content;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.totalElements = totalElements;
		this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, currentPage, pageSize, totalElements, totalPages);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PagedResult<?> other = (PagedResult<?>) obj;
		return Objects.equals(content, other.content) && currentPage == other.currentPage && pageSize == other.pageSize
				&& totalElements == other.totalElements && totalPages == other.totalPages;
	}

	@Override
	public String toString() {
		return "PagedResult [content=" + content + ", currentPage=" + currentPage + ", pageSize=" + pageSize
				+ ", totalElements=" + totalElements + ", totalPages=" + totalPages + "]";
	}

}
