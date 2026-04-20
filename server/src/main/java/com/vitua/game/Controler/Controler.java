package com.vitua.game.Controler;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PostExchange;

import com.vitua.game.DTO.GameResponceDTO;
import com.vitua.game.Service.GameService;

@RestController 
@RequestMapping("game")
public class Controler {
    
    private final GameService service;

    public Controler(GameService service) {
        this.service = service;
    }

    @GetMapping("/")
    public GameResponceDTO test() {
        return service.test();
    }
     @GetMapping("/all")
    public List<GameResponceDTO> testAll() {
        return service.testAll();
    }
    @PostMapping("/")
    public GameResponceDTO testPost(@RequestBody GameResponceDTO data) {
        System.err.println(data.x());
        System.err.println(data.y());
        return service.test();
    }

}