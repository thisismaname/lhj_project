package com.example.api.mapper;

import com.example.api.model.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 仓库库存 MyBatis Mapper
 */
@Mapper
public interface InventoryMapper {

    Inventory findByWidAndCid(@Param("wid") String wid, @Param("cid") String cid);

    /**
     * 仓库无此商品则插入，已有则原子累加。
     * 依赖 inventory 表上的 (wid, cid) 唯一键，否则并发下仍会插入重复行。
     * 插入分支的 id 由调用方传入（原生 SQL 不走 Hibernate 主键生成器）。
     */
    int upsertIncrease(@Param("id") String id,
                       @Param("wid") String wid,
                       @Param("cid") String cid,
                       @Param("name") String name,
                       @Param("delta") int delta);

}
