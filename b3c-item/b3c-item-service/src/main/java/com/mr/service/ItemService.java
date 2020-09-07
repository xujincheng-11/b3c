package com.mr.service;

import com.mr.service.pojo.Item;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @ClassName ItemService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/14
 * @Version V1.0
 **/
@Service
public class ItemService {

    public Item saveItem(Item item){
        
          item.setId(new Random().nextInt());
          return item;
    }
}
