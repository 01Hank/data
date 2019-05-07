package com.ego.item.service;

import com.ego.item.mapper.CategoryMapper;
import com.ego.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/2
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public List<Category> queryListByPid(Long pid) {

        Category category = new Category();
        category.setParentId(pid);

        return categoryMapper.select(category);
    }

    @Override
    public List<String> queryListByCids(List<Long> cids) {
        List<Category> categories = categoryMapper.selectByIdList(cids);
        return categories.stream().map(c->c.getName()).collect(Collectors.toList());
    }

    @Override
    public List<Category> queryListByIds(List<Long> idList) {
        return categoryMapper.selectByIdList(idList);
    }
}
