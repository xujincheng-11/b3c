package com.mr.service;

import com.mr.mapper.SpecParamMapper;
import com.mr.service.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecParamService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/24
 * @Version V1.0
 **/
@Service
public class SpecParamService {
    @Resource
    private SpecParamMapper specParamMapper;

   //查询
    public List<SpecParam> querySpecParams(Long gid,Long cid,Boolean searching, Boolean generic){
        /*第一种方法
        SpecParam t = new SpecParam();
        t.setGroupId(gid);
        return this.specParamMapper.select(t);*/

        //第二种方法; 根据分类查詢规模组
        //创建过滤条件类
        Example example = new Example(SpecParam.class);

        //通过过滤条件类,构建实例,实体字段和参数相等
        Example.Criteria  criteria= example.createCriteria();

        criteria.andEqualTo("groupId",gid);
        criteria.andEqualTo("cid",cid);
        criteria.andEqualTo("searching",searching);
        criteria.andEqualTo("generic",generic);
        //设置条件查询  根据实体中的属性,那个不为空就查询那个
        return specParamMapper.selectByExample(example);
    }

    //增加
   public ResponseEntity saveOrUpdateSpecParam(SpecParam specParam){
        if(specParam != null){
            if(specParam.getId() !=null && specParam.getId() != 0){
                return ResponseEntity.ok(specParamMapper.updateByPrimaryKey(specParam));
            }else{

                return ResponseEntity.ok(specParamMapper.insertSelective(specParam));
            }
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    //删除
    public ResponseEntity findById(Long id) {
        if (id != null && id != 0){
            return ResponseEntity.ok(specParamMapper.deleteByPrimaryKey(id));
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
