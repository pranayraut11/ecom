package com.ecom.shared.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("app")
public class AppController {
    @GetMapping("started")
    public ResponseEntity<Void> health(){
        return ResponseEntity.ok().build();
    }
}
