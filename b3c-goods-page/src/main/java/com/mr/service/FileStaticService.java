package com.mr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;

/**
 * @ClassName FileStaticService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/18
 * @Version V1.0
 **/
@Service
public class
FileStaticService {
    //查询model数据
    @Autowired
    private ItemService itemService;

    //注入静态化模版
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${b2c.thymeleaf.destPath}")
    private String destPath;// 保存的页面路径

    /**
     * 创建/修改html页面
     * @param id
     * @throws Exception
     */
    public void createStaticHtml(Long id) throws Exception {
        // 创建上下文context
        Context context = new Context();
        // 把数据加入上下文context
        context.setVariables(this.itemService.toGoodInfo(id));
        // 创建最终文件输出流，指定文件夹目录，文件名 后缀 等
        File itemHtml = new File(destPath,id + ".html");
        //创建输出流 指定格式
        PrintWriter printWriter=  new PrintWriter(itemHtml,"UTF-8");
        // 利用thymeleaf模板引擎将上下文结合模版生成到指定到文件中
        templateEngine.process("item", context, printWriter);
    }
    /**
     * 删除html页面
     * */
    public void deleteHtml(Long id) {
        // 创建最终文件输出流，指定文件夹目录，文件名 后缀 等
        File file = new File(destPath,id + ".html");
        //file.deleteOnExit();
        file.delete();
    }
}
