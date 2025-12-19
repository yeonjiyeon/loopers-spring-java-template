package com.loopers.core.cache.page;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class PageWrapper<T> {
  private List<T> content;
  private long totalElements;
  private int pageNumber;
  private int pageSize;

  public PageWrapper() {}

  public PageWrapper(Page<T> page) {
    this.content = page.getContent();
    this.totalElements = page.getTotalElements();
    this.pageNumber = page.getNumber();
    this.pageSize = page.getSize();
  }

  public Page<T> toPage() {
    return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
  }

  public List<T> getContent() { return content; }
  public long getTotalElements() { return totalElements; }
  public int getPageNumber() { return pageNumber; }
  public int getPageSize() { return pageSize; }
}
