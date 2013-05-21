package com.alibaba.doris.admin.web.configer.util;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.doris.admin.web.configer.support.PageView;

public class PageViewUtil {

    public static PageView buildPageView(int currentpage, int totalLines) {
        PageView pv = new PageView();
        pv.setCurrentPage(currentpage);
        pv.setTotalLines(totalLines);
        int totalPages = 0;
        if (totalLines > 0) {
            if (totalLines % WebConstant.DEFAULT_ITEMS_PER_PAGE > 0) {
                totalPages = (totalLines / WebConstant.DEFAULT_ITEMS_PER_PAGE) + 1;
            } else {
                totalPages = totalLines / WebConstant.DEFAULT_ITEMS_PER_PAGE;
            }
        }
        pv.setTotalPages(totalPages);
        List<Integer> displayList = new ArrayList<Integer>();
        int pageIndex = 0;
        while ((++pageIndex) <= totalPages) {
            displayList.add(pageIndex);
        }
        pv.setDisplayList(displayList);
        return pv;
    }
}
