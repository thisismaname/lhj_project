package com.example.api.mapper;

import com.example.api.model.entity.Commodity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 商品 MyBatis Mapper
 *
 * <p>注意：本接口独立放在 mapper 包，切勿对 repository 包使用 @MapperScan，
 * 否则 MyBatis 会把 JpaRepository 接口也注册成 Mapper Bean，与 Spring Data 代理冲突。
 */
@Mapper
public interface CommodityMapper {

    int insert(Commodity commodity);

    int update(Commodity commodity);

    int deleteById(@Param("id") String id);

    Commodity findById(@Param("id") String id);

    List<Commodity> findAll();

    Commodity findByName(@Param("name") String name);

    /**
     * 模糊查询，调用方需自行传入带 % 的 pattern
     */
    List<Commodity> findByNameLike(@Param("name") String name);

    /**
     * 原子自增/自减商品总库存，delta 可为负。
     * 由数据库在单条语句内完成读-改-写，不存在并发丢失更新。
     *
     * @return 受影响行数，0 表示商品不存在
     */
    int addCount(@Param("id") String id, @Param("delta") int delta, @Param("now") Date now);

}
