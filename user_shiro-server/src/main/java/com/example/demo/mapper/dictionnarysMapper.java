package com.example.demo.mapper;

import java.util.List;
import com.example.demo.model.dictionnarys;

public interface dictionnarysMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(dictionnarys record);

    dictionnarys selectByPrimaryKey(Integer id);

    List<dictionnarys> selectAll();

    int updateByPrimaryKey(dictionnarys record);
}