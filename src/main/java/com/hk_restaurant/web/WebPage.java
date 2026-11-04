package com.hk_restaurant.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebPage {
    @GetMapping({"/", "/login"}) public String index() { return "index"; }
}
