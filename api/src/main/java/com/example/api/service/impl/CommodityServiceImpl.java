package com.example.api.service.impl;

import com.example.api.model.entity.Commodity;
import com.example.api.repository.CommodityRepository;
import com.example.api.service.CommodityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CommodityServiceImpl implements CommodityService {

    @Resource
    private CommodityRepository commodityRepository;

    @Override
    public Commodity save(Commodity commodity) {
        commodity.setCreateAt(new Date());
        commodity.setUpdateAt(new Date());
        return commodityRepository.save(commodity);
    }

    @Override
    public void update(Commodity commodity) {
        commodityRepository.save(commodity);
    }

    @Override
    public void delete(String id) {
        commodityRepository.deleteById(id);
    }

    @Override
    public Commodity findById(String id) {
        return commodityRepository.findById(id).orElse(null);
    }

    @Override
    public List<Commodity> findAll() {
        return commodityRepository.findAll();
    }

    @Override
    public List<Commodity> findAllByLikeName(String name) {
        return commodityRepository.findByNameLike("%" + name + "%");
    }

}
