package com.mr.service.bo;

import com.mr.service.pojo.SpecGroup;
import com.mr.service.pojo.SpecParam;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SpecBo
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/8/17
 * @Version V1.0
 **/
@Data
public class SpecBo extends SpecGroup {

    private List<SpecParam> specParamList;
}
