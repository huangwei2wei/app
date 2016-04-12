package com.app.db.mysql.page;
import java.util.ArrayList;
import java.util.List;
/**
 * 分页
 * 
 * @author sunzx
 * @version 1.0
 * 
 */
public class PageList {
    /** 当前页码*/
    private int            pageIndex = 0;
    /** 每页面显示数目*/
    private int            pageSize  = 25;
    /** 总页数*/
    private int            fullListSize;
    /** 内容列表*/
    protected List<Object> list;

    public PageList() {
        list = new ArrayList<Object>();
    }

    public PageList(int fullListSize, List<Object> list, int pageSize, int pageIndex) {
        this.fullListSize = fullListSize;
        this.pageSize = pageSize;
        this.list = list;
        this.pageIndex = pageIndex;
    }

    public int getFullListSize() {
        return fullListSize;
    }

    public void setFullListSize(int fullListSize) {
        this.fullListSize = fullListSize;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalSize() {
        int totalSize = fullListSize / pageSize;
        return fullListSize % pageSize > 0 ? totalSize + 1 : totalSize;
    }

    public boolean getHasNext() {
        return (pageIndex + 1) < this.getTotalSize();
    }

    public boolean getHasPrev() {
        return pageIndex > 0;
    }
}
