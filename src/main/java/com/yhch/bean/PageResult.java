package com.yhch.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * PageResult
 * Created by zlren on 2017/2/27.
 */
public class PageResult {

    // 定义jackson对象
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Integer pageCurrent; // 当前查询的是第几页
    private Integer rowCount; // 当前这一页实际查询结果共有多少行
    private List<?> dataList; // 实际数据
    private Integer pageCount; // 一共有多少页
    private Long rowTotal; // 共有多少条记录

    public Integer getPageCurrent() {
        return pageCurrent;
    }

    public void setPageCurrent(Integer pageCurrent) {
        this.pageCurrent = pageCurrent;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public List<?> getDataList() {
        return dataList;
    }

    public void setDataList(List<?> dataList) {
        this.dataList = dataList;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Long getRowTotal() {
        return rowTotal;
    }

    public void setRowTotal(Long rowTotal) {
        this.rowTotal = rowTotal;
    }

    public PageResult(Integer pageCurrent, Integer rowCount, List<?> dataList, Integer pageCount, Long rowTotal) {
        this.pageCurrent = pageCurrent;
        this.rowCount = rowCount;
        this.dataList = dataList;
        this.pageCount = pageCount;
        this.rowTotal = rowTotal;
    }

    /**
     * 写一个pageinfo为参数的构造方法
     * @param pageInfo
     */
    public PageResult(PageInfo<?> pageInfo) {
        this.dataList = pageInfo.getList();
        this.pageCurrent = pageInfo.getPageNum();
        this.rowCount = dataList.size();
        this.pageCount = pageInfo.getPages();
        this.rowTotal = pageInfo.getTotal();
    }

}
