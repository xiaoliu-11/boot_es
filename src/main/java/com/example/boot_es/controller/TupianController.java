package com.example.boot_es.controller;


import com.example.boot_es.pojo.Tupian;
import com.example.boot_es.service.TupianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class TupianController {


    @Autowired
    private TupianService tupianService;





    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws Exception{
         return tupianService.parseTupian(keyword);

    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
   public List<Map<String,Object>> search(
          @PathVariable("keyword") String keyword,
          @PathVariable("pageNo") int pageNo,
          @PathVariable("pageSize") int pageSize

    ) throws IOException {

 return tupianService.searchPage(keyword,pageNo,pageSize);

   }


}
