package com.lion.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @GetMapping("/hello")
    @ResponseBody
    public String aa() {
        return "<h1> hello0!! </h1>";
    }
}
