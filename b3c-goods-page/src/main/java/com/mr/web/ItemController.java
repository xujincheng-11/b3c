package com.mr.web;

import com.mr.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @ClassName ItemController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/17
 * @Version V1.0
 **/
@Controller
@RequestMapping("item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    /*
    * 根据前台传的spuId跳转商品详情页面
    * */
    @GetMapping("{id}.html")
    public String toGoodInfo(@PathVariable("id") Long spuId, ModelMap map){
        Map<String,Object> itemMap = itemService.toGoodInfo(spuId);
        map.putAll(itemMap);
        return "item";
    }
}
