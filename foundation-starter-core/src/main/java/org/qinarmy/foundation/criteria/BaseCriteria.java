package org.qinarmy.foundation.criteria;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.qinarmy.foundation.util.TimeUtils.DATE_TIME_FORMAT;

/**
 * created  on 2019-03-15.
 */
public class BaseCriteria extends Form {


    private Integer offset;

    private Integer rowCount;

    /**
     * true 表示需要返回总行数
     */
    private Boolean queryRowCount;

    /**
     * true 表示 时间正序,false 表示时间倒序.
     * 下层默认是按时间倒序
     */
    private Boolean ascOrder;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime startCreateTime;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime endCreateTime;

    /**
     * 分页查询中上一页的最后一行 id
     */
    private Long lastId;

    private Long clientOperatorId;

    private String clientOperatorName;

    public Integer getOffset() {
        if(offset == null){
            offset = 0;
        }
        return offset;
    }

    public BaseCriteria setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getRowCount() {
        if(rowCount == null){
            rowCount = 10;
        }
        return rowCount;
    }

    public BaseCriteria setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    public Boolean getQueryRowCount() {
        return queryRowCount;
    }

    public BaseCriteria setQueryRowCount(Boolean queryRowCount) {
        this.queryRowCount = queryRowCount;
        return this;
    }

    public Boolean getAscOrder() {
        if(ascOrder == null){
            ascOrder = Boolean.FALSE;
        }
        return ascOrder;
    }

    public BaseCriteria setAscOrder(Boolean ascOrder) {
        this.ascOrder = ascOrder;
        return this;
    }

    public LocalDateTime getStartCreateTime() {
        return startCreateTime;
    }

    public BaseCriteria setStartCreateTime(LocalDateTime startCreateTime) {
        this.startCreateTime = startCreateTime;
        return this;
    }

    public LocalDateTime getEndCreateTime() {
        return endCreateTime;
    }

    public BaseCriteria setEndCreateTime(LocalDateTime endCreateTime) {
        this.endCreateTime = endCreateTime;
        return this;
    }

    public Long getLastId() {
        return lastId;
    }

    public BaseCriteria setLastId(Long lastId) {
        this.lastId = lastId;
        return this;
    }

    public Long getClientOperatorId() {
        return clientOperatorId;
    }

    public BaseCriteria setClientOperatorId(Long clientOperatorId) {
        this.clientOperatorId = clientOperatorId;
        return this;
    }

    public String getClientOperatorName() {
        return clientOperatorName;
    }

    public BaseCriteria setClientOperatorName(String clientOperatorName) {
        this.clientOperatorName = clientOperatorName;
        return this;
    }
}
