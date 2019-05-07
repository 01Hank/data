package com.ego.search.controller;

import com.ego.common.pojo.PageResult;
import com.ego.search.pojo.Goods;
import com.ego.search.pojo.SearchRequest;
import com.ego.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/12
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RestController
public class SearchController {
    @Autowired
    private GoodsService goodsService;
    @PostMapping("/page")
    public ResponseEntity<PageResult<Goods>> page(@RequestBody SearchRequest searchRequest)
    {
        PageResult<Goods> result =  goodsService.page(searchRequest);
        return ResponseEntity.ok(result);
    }
}
