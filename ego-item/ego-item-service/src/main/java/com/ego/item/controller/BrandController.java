package com.ego.item.controller;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Brand;
import com.ego.item.service.BrandService;
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
 * @create 2019/4/4
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;


//    descending=false&page=1&rowsPerPage=5&sortBy=id&search=G
    @GetMapping("/page")
    public ResponseEntity<PageResult> page(@RequestParam("descending") Boolean descending,
                                           @RequestParam(value = "page",defaultValue = "1") Integer page,
                                           @RequestParam(value = "rowsPerPage",defaultValue = "5") Integer rowsPerPage,
                                           @RequestParam("sortBy") String sortBy,
                                           @RequestParam("search") String search
                                           )
    {
        PageResult<Brand> result = brandService.page(descending, page, rowsPerPage, sortBy, search);
        if(result==null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> save(Brand brand,@RequestParam("cids")String cids)
    {
        brandService.save(brand,cids);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryByCid(@PathVariable("cid") Long cid) {
        return ResponseEntity.ok(brandService.findByCid(cid));
    }

    @GetMapping("/ids/list")
    List<Brand> queryListByIds(@RequestParam("ids") List<Long> idList){
        return brandService.queryListByIds(idList);
    }
}
