package com.ego.item.service;

import com.ego.item.mapper.SpecificationMapper;
import com.ego.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/8
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Service
public class SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    public Specification findByCid(Long cid) {
        return specificationMapper.selectByPrimaryKey(cid);
    }
}
