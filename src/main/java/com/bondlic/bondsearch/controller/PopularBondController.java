package com.bondlic.bondsearch.controller;

import com.bondlic.bondsearch.model.Bond;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/popular-bonds")
public class PopularBondController {

    @GetMapping
    public List<Bond> getPopularBond() {
        Bond first = Bond.builder()
                .isin("123")
                .name("First")
                .build();
        return List.of(first);
    }
}
