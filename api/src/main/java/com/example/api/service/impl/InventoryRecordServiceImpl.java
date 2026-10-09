package com.example.api.service.impl;

import com.example.api.mapper.CommodityMapper;
import com.example.api.mapper.InventoryMapper;
import com.example.api.mapper.InventoryRecordMapper;
import com.example.api.model.entity.Commodity;
import com.example.api.model.entity.Inventory;
import com.example.api.model.entity.InventoryRecord;
import com.example.api.model.vo.CommodityChartVo;
import com.example.api.repository.CommodityRepository;
import com.example.api.repository.InventoryRecordRepository;
import com.example.api.repository.InventoryRepository;
import com.example.api.service.InventoryRecordService;
import com.example.api.utils.DataTimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class InventoryRecordServiceImpl implements InventoryRecordService {

    @Resource
    private InventoryRepository inventoryRepository;

    @Resource
    private CommodityRepository commodityRepository;

    @Resource
    private InventoryRecordRepository recordRepository;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private InventoryMapper inventoryMapper;

    @Resource
    private InventoryRecordMapper inventoryRecordMapper;

    @Override
    public List<CommodityChartVo> analyzeCommodity(Integer type) {
        List<CommodityChartVo> result = new ArrayList<>();
        List<InventoryRecord> all = recordRepository.findAllByType(type);
        Map<String, Integer> map = new HashMap<>();
        for (InventoryRecord r : all) {
            if (map.containsKey(r.getName())) {
                map.put(r.getName(), map.get(r.getName()) + r.getCount());
            } else {
                map.put(r.getName(), r.getCount());
            }
        }
        for (String key : map.keySet()) {
            result.add(new CommodityChartVo(map.get(key), key));
        }
        return result;
    }

    @Override
    public List<InventoryRecord> findAllByWarehouseId(String wid) {
        return recordRepository.findAllByWid(wid);
    }

    @Override
    public List<InventoryRecord> findAllByCommodityId(String cid) {
        return recordRepository.findAllByCid(cid);
    }

    @Override
    public InventoryRecord out(InventoryRecord record) throws Exception {

        //查找当前商品在该仓库的库存
        Inventory inventory = inventoryRepository.findByWidAndCid(record.getWid(), record.getCid());
        //查询结果为空
        if (inventory == null) throw new Exception("仓库内不存在该商品");
        //比较库存
        if (inventory.getCount() < record.getCount()) throw new Exception("出库失败，库存数量不足");

        Optional<Commodity> optional = commodityRepository.findById(record.getCid());
        if (optional.isEmpty()) {
            throw new Exception("不存在的商品id");
        }
        Commodity commodity = optional.get();
        commodity.setUpdateAt(new Date());
        commodity.setCount(commodity.getCount() - record.getCount());
        commodityRepository.save(optional.get());
        inventory.setCount(inventory.getCount() - record.getCount());
        inventoryRepository.save(inventory);
        record.setType(-1);
        record.setCreateAt(new Date());
        return recordRepository.save(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InventoryRecord in(InventoryRecord record) throws Exception {

        //入库数量必须为正，否则会把库存改小甚至改负
        if (record.getCount() == null || record.getCount() <= 0) {
            throw new Exception("入库数量必须大于0");
        }

        Date now = new Date();

        //1) 原子自增商品总库存；受影响行数为 0 说明商品不存在
        //   加锁顺序 商品 -> 仓库，与 out() 保持一致，避免死锁
        if (commodityMapper.addCount(record.getCid(), record.getCount(), now) == 0) {
            throw new Exception("不存在的商品id");
        }

        //2) 原子自增仓库库存，该仓库无此商品时自动插入
        //   upsert 依赖 inventory 表 (wid, cid) 唯一键
        inventoryMapper.upsertIncrease(
                UUID.randomUUID().toString().replace("-", ""),
                record.getWid(), record.getCid(), record.getName(), record.getCount());

        //3) 写流水
        if (record.getId() == null || record.getId().isEmpty()) {
            record.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        record.setType(1);
        record.setCreateAt(now);
        inventoryRecordMapper.insert(record);

        return record;
    }

}
