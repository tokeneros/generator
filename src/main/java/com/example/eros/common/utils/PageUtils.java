package com.example.eros.common.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 分页信息
 */
public class PageUtils implements Serializable{

    //总页数
    private int count;
    //数据
    private List<?> rows;

    public PageUtils(List<?> list, int total){
         this.rows = list;
         this.count = total;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
