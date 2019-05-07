package com.ego.user.controller;

import com.ego.user.pojo.User;
import com.ego.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/18
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable("data") String data, @PathVariable("type") Integer type) {

        Boolean result = userService.check(data, type);
        return ResponseEntity.ok(result);

    }

    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone)
    {
        userService.sendCode(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code)
    {
        userService.register(user,code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/query")
    public ResponseEntity<User> query(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userService.findByUP(username, password);
        if(user==null)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }

}
