package com.ego.item.controller;

import com.ego.item.pojo.Ms;
import com.ego.item.service.MsService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Ms")
public class MsController {
    @Autowired
    private MsService msService;

    @GetMapping("/getM")
    public ResponseEntity<PageInfo<Ms>> getM(){
        PageInfo<Ms> msPageInfo = msService.pageMs();
        return ResponseEntity.ok(msPageInfo);
    }

    @GetMapping("/Parice")
    public ResponseEntity Parice(@RequestParam("id")Long id){
        int index = msService.Parice(id);
        if(index==1){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(0);
    }
}
