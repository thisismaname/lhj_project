package com.example.api.mapper;

import com.example.api.model.entity.InventoryRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出入库流水 MyBatis Mapper
 */
@Mapper
public interface InventoryRecordMapper {

    int insert(InventoryRecord record);

}
