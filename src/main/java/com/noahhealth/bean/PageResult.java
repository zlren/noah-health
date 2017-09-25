package com.noahhealth.bean;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * PageResult
 * Created by zlren on 2017/5/27.
 */
@Data
public class PageResult {

    private Integer rowCount;   // 当前这一页实际查询结果共有多少行
    private List<?> data;       // 实际数据
    private Integer pageTotal;  // 一共有多少页
    private Long rowTotal;      // 共有多少条记录

    /**
     * 写一个pageinfo为参数的构造方法
     *
     * @param pageInfo
     */
    public PageResult(PageInfo<?> pageInfo) {
        this.data = pageInfo.getList();
        this.rowCount = data.size();
        this.pageTotal = pageInfo.getPages();
        this.rowTotal = pageInfo.getTotal();
    }
}
