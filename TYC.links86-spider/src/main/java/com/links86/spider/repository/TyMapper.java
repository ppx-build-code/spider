package com.links86.spider.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by xumengyi on 21/08/2018.
 */
@Mapper
public interface TyMapper {

    @Select("SELECT * FROM company_ty_url WHERE flag = 0 and com_name like #{city} and id >= (SELECT FLOOR( MAX(id) * RAND()) FROM company_ty_url ) ORDER BY id LIMIT 5")
    List<Map<String, String>> getTyDirectlyUrl(@Param("city") String city,
                                               @Param("limit") int limit);

    @Update("update company_ty_url set flag=1 where com_id=#{id}")
    void updateFlag(@Param("id") String id);
}
