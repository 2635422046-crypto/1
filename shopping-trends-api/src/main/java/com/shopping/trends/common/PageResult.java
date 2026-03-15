package com.shopping.trends.common;

import java.util.List;

/**
 * 分页结果封装类
 */
public class PageResult<T> {
    private long pageNum;    // 当前页
    private long pageSize;   // 每页数量
    private long totalPage;  // 总页数
    private long total;         // 总记录数
    private List<T> list;       // 分页数据列表

    // 构造方法
    public PageResult() {
    }

    public static <T> PageResult<T> of(long pageNum, long pageSize, long total, List<T> list) {
        PageResult<T> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(total);
        result.setList(list);
        result.setTotalPage((int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Deprecated
    public PageResult(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        this.totalPage = (int) Math.ceil((double) total / pageSize);
    }

    // Getter和Setter方法
    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(long totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}