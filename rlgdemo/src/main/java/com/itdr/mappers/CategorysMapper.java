package com.itdr.mappers;

import com.itdr.pojo.Categorys;

public interface CategorysMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Categorys record);

    int insertSelective(Categorys record);

    Categorys selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Categorys record);

    int updateByPrimaryKey(Categorys record);
}