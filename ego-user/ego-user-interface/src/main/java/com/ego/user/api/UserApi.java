package com.ego.user.api;

import com.ego.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/4/20
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
public interface UserApi {
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable("data") String data, @PathVariable("type") Integer type);

    @PostMapping("/code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone);

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code);

    @GetMapping("/query")
    public ResponseEntity<User> query(@RequestParam("username") String username, @RequestParam("password") String password) ;
}
