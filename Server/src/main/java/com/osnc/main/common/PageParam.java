package com.osnc.main.common;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {
    @NonNull
    private Integer current = 1;
    @NonNull
    private Integer size = 10;

    protected List<OrderItem> orders;// 降序（大到小、晚到早）DESC 或 升序（小到大、早到晚）ASC

    public PageParam(int current, int size, List<OrderItem> order) {
    }

    public <T> Page<T> toPage() {
        Page<T> page = new Page<>();
        page.setSize(this.size);
        page.setCurrent(this.current);
        page.setOrders(this.orders);
        return page;
    }
}
