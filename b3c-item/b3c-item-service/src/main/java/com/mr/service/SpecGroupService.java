package com.mr.service;

import com.mr.mapper.SpecGroupMapper;
import com.mr.service.pojo.SpecGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecGroupService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/24
 * @Version V1.0
 **/
@Service
public class SpecGroupService {
    @Resource
    private SpecGroupMapper specGroupMapper;

    //查询
    public List<SpecGroup> querySpecGroup(Long cid){
         /*第一种方法
        SpecGroup t = new SpecGroup();
        t.setCid(cid);
        return this.specGroupMapper.select(t);*/

            //根据分类查詢规模组
        //创建过滤条件类  第二种方法;
        Example example = new Example(SpecGroup.class);

        //通过过滤条件类,构建实例,实体字段和参数相等
        example.createCriteria().andEqualTo("cid",cid);
        //设置条件查询
        return specGroupMapper.selectByExample(example);
    }

    //增加
    public ResponseEntity saveOrUpdateSpecGroup(SpecGroup specGroup){
        if(specGroup != null){
            if(specGroup.getId() !=null && specGroup.getId() != 0){
                return ResponseEntity.ok(specGroupMapper.updateByPrimaryKey(specGroup));
            }else{

                return ResponseEntity.ok(specGroupMapper.insertSelective(specGroup));
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    //删除
    public ResponseEntity findById(Long id) {
        try {
            if(id !=null && id != 0){

                specGroupMapper.deleteByPrimaryKey(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("删除成功");
    }
}
