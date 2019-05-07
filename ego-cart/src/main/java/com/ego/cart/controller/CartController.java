package com.ego.cart.controller;

import com.ego.cart.pojo.Cart;
import com.ego.cart.service.CartService;
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
 * @create 2019/4/21
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RestController
public class CartController {
    @Autowired
    private CartService cartService;
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        try
        {
            cartService.addCart(cart);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    public ResponseEntity<List<Cart>> list(){
        try
        {
            List<Cart> result = cartService.list();
            return ResponseEntity.ok(result);
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart)
    {
        cartService.updateNum(cart);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> delete(@PathVariable("skuId") String skuId)
    {
        cartService.deleteCart(skuId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
