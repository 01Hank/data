package com.ego.item.service;

import com.ego.common.pojo.PageResult;
import com.ego.item.mapper.*;
import com.ego.item.pojo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;


    public PageResult page(String key, Integer saleable, Integer page, Integer rows) {

        PageHelper.startPage(page,Math.min(rows,100));

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotBlank(key))
        {
            criteria.andLike("title", "%" + key + "%").orLike("subTitle","%" + key + "%");
        }

        if(saleable!=null)
        {
            criteria.andEqualTo("saleable", saleable);
        }

        Page<Spu> pageInfo = (Page<Spu>)spuMapper.selectByExample(example);

        //将SpuList  -->  SpuBOList
        List<SpuBO> spuBOList = pageInfo.stream().map(spu -> {
            SpuBO spuBO = new SpuBO();
            try {
                BeanUtils.copyProperties(spuBO, spu);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return spuBO;
        })
                //将所有转换好的spuBo存入一个list中
                .collect(Collectors.toList());

        spuBOList.forEach(spuBO->{
            List<Long> cids = Arrays.asList(spuBO.getCid1(), spuBO.getCid2(), spuBO.getCid3());
            //通过类别id查询类别名字 ["类别1","类别2"]
            List<String> categoryNameList = categoryService.queryListByCids(cids);
            String categoryName = "";
            for(int i = 0 ;i < categoryNameList.size();i++)
            {
                categoryName+=categoryNameList.get(i);
                if(i < categoryNameList.size())
                {
                    categoryName += "/";
                }
            }
            //["类别1","类别2"] --> 类别1/类别2
            spuBO.setCategoryName(categoryName);


            //通过品牌id查询品牌名字
            Brand brand = brandMapper.selectByPrimaryKey(spuBO.getBrandId());
            spuBO.setBrandName(brand.getName());

        });


        return new PageResult(pageInfo.getTotal(),Long.valueOf(pageInfo.getPages()),spuBOList);
    }

    /**
     * 新增商品
     * @param spuBO
     */
    @Transactional
    public void save(SpuBO spuBO) {
        //新增spu
        spuBO.setCreateTime(new Date());
        spuBO.setLastUpdateTime(spuBO.getCreateTime());
        spuMapper.insertSelective(spuBO);
        //新增spudetail
        SpuDetail spuDetail = spuBO.getSpuDetail();
        spuDetail.setSpuId(spuBO.getId());
        spuDetailMapper.insertSelective(spuDetail);
        //新增skus
        saveSkuAndStock(spuBO);

        //发送信息到mq
        this.amqpTemplate.convertAndSend("item.insert" , spuBO.getId());
//        amqpTemplate.convertAndSend("exchange.ego.item","item.insert",spuBO.getId());
    }

    public SpuDetail findSpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    @Transactional
    public void update(SpuBO spuBO) {
        //更新spu
        spuBO.setLastUpdateTime(new Date());
        spuMapper.updateByPrimaryKey(spuBO);
        //更新spuDetail
        SpuDetail spuDetail = spuBO.getSpuDetail();
        spuDetailMapper.updateByPrimaryKey(spuDetail);

        //删除之前的stock(根据skuIds删除stock)
        Sku sku = new Sku();
        sku.setSpuId(spuBO.getId());
        List<Sku> skus = skuMapper.select(sku);

        List<Long> skuIds = skus.stream().map(s -> s.getId()).collect(Collectors.toList());

        Example example = new Example(Stock.class);
        example.createCriteria().andIn("skuId", skuIds);
        stockMapper.deleteByExample(example);
        //删除之前的skus(根据spuId删除所有sku)
        skuMapper.delete(sku);
        //更新skus
        saveSkuAndStock(spuBO);

        //发送信息到mq
        amqpTemplate.convertAndSend("exchange.ego.item","item.update",spuBO.getId());
    }

    /**
     * 保存sku以及库存
     * @param spuBO
     */
    private void saveSkuAndStock(SpuBO spuBO) {
        List<Sku> skus = spuBO.getSkus();
        if(skus!=null)
        {
            skus.forEach(sku->{
                sku.setSpuId(spuBO.getId());

                sku.setCreateTime(spuBO.getCreateTime());
                sku.setLastUpdateTime(sku.getCreateTime());
                skuMapper.insertSelective(sku);

                //新增stock
                Stock stock = sku.getStock();
                stock.setSkuId(sku.getId());

                stockMapper.insert(stock);
            });
        }
    }

    public List<Sku> findSkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);

        //查询库存数据
        if(skus!=null)
        {
            skus.forEach(s->{
                Stock stock = stockMapper.selectByPrimaryKey(s.getId());
                s.setStock(stock);
            });
        }

        return skus;
    }

    public SpuBO queryGoodsById(Long spuId) {
        SpuBO spuBO = new SpuBO();
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        //spuDetail
        spuBO.setSpuDetail(spuDetailMapper.selectByPrimaryKey(spuId));
        //拷贝其他相同属性值
        try {
            BeanUtils.copyProperties(spuBO, spu);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //查询skus
        spuBO.setSkus(this.findSkuBySpuId(spuId));
        return  spuBO;
    }

    public Sku querySkuById(String skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }

//    public List<Sku> pageMs() {
////        PageHelper.startPage(page,Math.min(rows,100));
////
////        Example example = new Example(Sku.class);
//
////        Page<Sku> pageInfo = (Page<Sku>)skuMapper.selectByExample(example);
//        List<Sku> ms = skuMapper.findMs();
//
//        return ms;
//   }
}
