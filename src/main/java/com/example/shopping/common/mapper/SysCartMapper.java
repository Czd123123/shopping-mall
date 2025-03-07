package com.example.shopping.common.mapper;

import com.example.shopping.common.entity.SysCart;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysCartMapper {
    List<SysCart> findByUserIdLimit(int userId);

    List<SysCart> findAll();

    int insert(int userId, int goodsId, int goodsNum);

    int deleteById(int id);

    int countByUserIdAndGoodsId(int userId, int goodsId);
}
