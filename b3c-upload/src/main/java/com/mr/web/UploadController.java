package com.mr.web;

import com.mr.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName UploadController
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/22
 * @Version V1.0
 **/
@RestController
@RequestMapping(value="upload")
public class UploadController {
    @Autowired
    private UploadService service;

    @PostMapping(value="image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        String url = service.uploadImage(file);
        return ResponseEntity.ok(url);
    }
}
