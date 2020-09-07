package com.test;

import com.mr.GoodsPageApplication;
import com.mr.client.GoodsClient;
import com.mr.common.utils.PageResult;
import com.mr.service.FileStaticService;
import com.mr.service.bo.SpuBo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName CreatePageTest
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/18
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { GoodsPageApplication.class})
public class CreatePageTest {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private FileStaticService fileService;

    @Test
    public void createPage(){
        //定义算法分批导入数据
        int page = 1;//从第0页导入数据
        boolean load = true; //判断状态
        int count = 0;//统计总数据
        while (load){
            //查询spu分页数据
            PageResult<SpuBo> spuPage=goodsClient.querySpuByPage(page++,10,2l,"");

            spuPage.getItems().forEach(spuBo -> {
                try {
                    //循环创建静态文件
                    fileService.createStaticHtml(spuBo.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            //统计数据总条数
            count+=spuPage.getItems().size();
            if(spuPage.getItems().size() == 0){
                load=false;
            }
            System.out.println("一共增加了"+count+"条");
        }
    }
}
