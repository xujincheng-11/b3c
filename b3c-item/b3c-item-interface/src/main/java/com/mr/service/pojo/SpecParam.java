package com.mr.service.pojo;

import lombok.Data;

import javax.persistence.*;

/**
 * @ClassName SpecGroup
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/24
 * @Version V1.0
 **/
@Data
@Table(name="tb_spec_param")
public class SpecParam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增策略
    private Long id;
    private Long cid;
    private Long groupId;
    private String name;
    @Column(name = "`numeric`")
    private Boolean numeric;
    private String unit;
    private Boolean generic;
    private Boolean searching;
    private String segments;
}
