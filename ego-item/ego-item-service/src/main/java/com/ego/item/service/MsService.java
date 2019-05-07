package com.ego.item.service;

import com.ego.item.Data.Length;
import com.ego.item.mapper.MsMapper;
import com.ego.item.pojo.Ms;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MsService {
    @Autowired
    private MsMapper msMapper;

    private List<Length> list = new ArrayList<>();

    public PageInfo<Ms> pageMs() {
        PageHelper.startPage(1,6);
//
//        Example example = new Example(Sku.class);

//        Page<Sku> pageInfo = (Page<Sku>)skuMapper.selectByExample(example);
        List<Ms> ms = msMapper.findMs();
        for (int i=0;i<ms.size();i++){
            Length L = new Length();
            Long id = ms.get(i).getId();
            Long mlenth = ms.get(i).getMlenth();
            L.setId(id);
            L.setLength(mlenth.intValue());
            list.add(L);
        }

        PageInfo<Ms> msPageInfo = new PageInfo<>(ms);
        return msPageInfo;
    }

    public int Parice(Long id) {
        int index;

        for (int i=0;i<list.size();i++){
            if(list.get(i).getId()==id || id.equals(list.get(i).getId())){
                index = list.get(i).getLength()-1;
                if(index==0){

                    msMapper.Parice(id);
                    list.remove(i);
                    return 0;
                }
                list.get(i).setLength(index);
                break;
            }
        }
        return 1;

    }
}
