package com.mr.service.pojo;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @ClassName Brand
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/17
 * @Version V1.0
 **/
@Data
@Table(name="tb_brand")
public class Brand implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)

    private Long id;

    private String name;

    private String image;

    private Character letter;

}
