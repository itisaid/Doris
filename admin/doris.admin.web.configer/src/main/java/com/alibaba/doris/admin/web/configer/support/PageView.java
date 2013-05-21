package com.alibaba.doris.admin.web.configer.support;

import java.util.List;

public class PageView {

    List<Integer> displayList;
    int           currentPage;
    int           totalPages;
    int           totalLines;

    public List<Integer> getDisplayList() {
        return displayList;
    }

    public void setDisplayList(List<Integer> displayList) {
        this.displayList = displayList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
    }

}
