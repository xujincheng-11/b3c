package com.mr.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.mr.web.UploadController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName UploadService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/22
 * @Version V1.0
 **/
@Service
public class UploadService {

    @Autowired
    FastFileStorageClient storageClient;
    @Value("${IMAGE_SERVER_DOMAIN}")//返回url的域名不能写死
    private String IMAGE_SERVER_DOMAIN;

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);


    //支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");
    /**
     * 1. 校验文件后缀 jpg , png,bmp等
     * 2. 校验文件的的内容,校验的是否是图片
     * 3. 校验上传图片到图片服务器(1.本机电脑, 2.分布式文件服务 3.fastdfs:巴里巴巴的产品)
     * */
    public String uploadImage(MultipartFile file) throws IOException {

        //1.本机电脑
      /*  File imgFolder = new File("D://upload//images");
        if(!imgFolder.exists()){//文件夹不存在就创建
            imgFolder.mkdirs();//建立多级文件夹
        }
        //把上传的文件写入刚刚创建的文件夹中+文件名,保存
        file.transferTo(new File("D://upload//images//"+file.getOriginalFilename()));
        //file.transferTo(new File(imgFolder,file.getOriginalFilename()));
        //return "http://image.b2c.com/"+file.getOriginalFilename();
        return "IMAGE_SERVER_DOMAIN"+file.getOriginalFilename();*/

        //通过fastDFS上传(Linux)
        //判断如果suffixes不包含这个文件的内容类型
        // 1、图片信息校验
        // 1)校验文件类型
        String type = file.getContentType();
        if (!suffixes.contains(type)) {
            logger.info("上传失败，文件类型不匹配：{}", type);
            return null;
        }
        // 2)校验图片内容
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            logger.info("上传失败，文件内容不符合要求");
            return null;
        }
        //判断上传文件的后缀名
        String imgName = file.getOriginalFilename();

        //截取方式1  截取点出现最后一次的后缀(不包含点.)
        //String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");

        // 截取方式2 截取上传文件的 .png; 查询(.)最后出现的一次+1;是从点开始截取(包头不包尾,所以加1)最后获得后缀
        String ex = imgName.substring(imgName.lastIndexOf(".")+1);

        //1. 上传;参数1.上传文件的输入流,2.上传文件的大小,3.上传文件的后缀
        StorePath storePath = storageClient.uploadFile(file.getInputStream(),file.getSize(),ex,null);

        // 2.上传  生成缩略图,保存两个文件,一大一小
        //StorePath storePath =storageClient.uploadImageAndCrtThumbImage(file.getInputStream(),file.getSize(),ex,null);
        //获得上传文件组的全路径
        String fullPath = storePath.getFullPath();
        //返回url的域名不能写死, 返回全路径
        return IMAGE_SERVER_DOMAIN+fullPath;
    }
}
