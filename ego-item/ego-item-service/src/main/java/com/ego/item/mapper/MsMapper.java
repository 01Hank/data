package com.ego.item.mapper;

import com.ego.item.pojo.Ms;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MsMapper extends Mapper<Ms> {
    @Select("SELECT * FROM tb_ms t1 INNER JOIN tb_sku t2 ON t1.id=t2.id")
    List<Ms> findMs();

    @Delete("DELETE FROM tb_ms WHERE id=#{id}")
    void Parice(@Param("id")Long id);
}
