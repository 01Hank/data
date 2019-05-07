package com.ego.item.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tb_ms")
@Data
public class Ms {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long Mid;
    private Date Mdata;
    private Long Mtime;
    private Long Mlenth;
    private  Long id;

    private Long id1;
    private Long spuId;
    private String title;
    private String images;
    private Long price;
    private String ownSpec;// 商品特殊规格的键值对
    private String indexes;// 商品特殊规格的下标
    private Boolean enable;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间
    private Date lastUpdateTime;// 最后修改时间
    private Stock stock;// 库存
}
