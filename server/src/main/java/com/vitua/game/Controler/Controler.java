package com.vitua.game.Controler;


import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PostExchange;

import com.vitua.game.DTO.GameResponceDTO;
import com.vitua.game.DTO.InputData;
import com.vitua.game.DTO.RegisterForm;
import com.vitua.game.Engine.InputRecord;
import com.vitua.game.Service.GameService;

@RestController 
@RequestMapping("game")
public class Controler {
    
    private final GameService service;

    public Controler(GameService service) {
        this.service = service;
    }
    @GetMapping("/all")
    public GameResponceDTO getAllPlayers(){
        return service.getAllPlayers("a");
        
    }
    @PostMapping("/all")
    public GameResponceDTO getAllPlayers(@RequestBody  InputData rec){
        service.injectInput(rec.nickName(), rec.data());
        return service.getAllPlayers(rec.nickName());
        
    }
    @PostMapping("/register")
    public boolean register(@RequestBody RegisterForm form){

        return service.register(form.name());
        
    }



}