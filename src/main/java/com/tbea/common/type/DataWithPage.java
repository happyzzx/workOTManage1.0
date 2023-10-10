package com.tbea.common.type;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataWithPage<T> {
    private List<T> data;
    private long total;
    private long pageSize;
    private long page;

    public static <T> DataWithPage<T> instance(Page<T> page) {
        DataWithPage<T> result = new DataWithPage<>();
        result.setData(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageSize(page.getSize());
        result.setPage(page.getCurrent());
        return result;
    }
    public static <T> DataWithPage<T> instance(long total, List<T> data) {
        DataWithPage<T> result = new DataWithPage<>();
        result.setTotal(total);
        result.setData(data);
        return result;
    }
    public static <T> DataWithPage<T> instance(long total, long pageSize, long page, List<T> data) {
        DataWithPage<T> result = new DataWithPage<>();
        result.setTotal(total);
        result.setPageSize(pageSize);
        result.setPage(page);
        result.setData(data);
        return result;
    }
}
