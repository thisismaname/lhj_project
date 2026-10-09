package com.example.api.repository;

import com.example.api.model.entity.Commodity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CommodityRepository extends JpaRepository<Commodity, String> {

    Commodity findByName(String name);

    List<Commodity> findByNameLike(String name);

    /**
     * 删除留言
     * @param id
     */
    void deleteMessageBoardInfo(String id);

}
