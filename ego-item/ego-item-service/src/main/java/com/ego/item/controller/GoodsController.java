package com.ego.item.controller;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.SpuBO;
import com.ego.item.pojo.SpuDetail;
import com.ego.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/8
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
//    key=x&saleable=1&page=1&rows=5
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBO>> page(@RequestParam("key")String key,
                                           @RequestParam("saleable")Integer saleable,
                                           @RequestParam(value = "page",defaultValue = "1")Integer page,
                                           @RequestParam(value = "rows",defaultValue = "5")Integer rows)
    {

        PageResult result = goodsService.page(key, saleable, page, rows);
        return  ResponseEntity.ok(result);
    }


    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpuBO spuBO){
        goodsService.save(spuBO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId)
    {
        return ResponseEntity.ok(goodsService.findSpuDetailBySpuId(spuId));
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody SpuBO spuBO)
    {
        goodsService.update(spuBO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id")Long spuId)
    {
        return ResponseEntity.ok(goodsService.findSkuBySpuId(spuId));
    }

//1
    @GetMapping("/spu/{id}")
    public SpuBO queryGoodsById(@PathVariable("id") Long spuId){
        return goodsService.queryGoodsById(spuId);
    }

    @GetMapping("/sku/{id}")
    Sku querySkuById(@PathVariable("id") String skuId){
        return goodsService.querySkuById(skuId);
    }
}
