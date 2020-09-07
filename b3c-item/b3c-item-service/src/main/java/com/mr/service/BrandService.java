package com.mr.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mr.common.enums.ExceptionEnums;
import com.mr.common.exception.MrException;
import com.mr.common.utils.PageResult;
import com.mr.mapper.BrandMapper;
import com.mr.service.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BrandService
 * @Description: TODO
 * @Author xujincheng
 * @Date 2020/7/17
 * @Version V1.0
 **/
@Service
public class BrandService {
    @Resource
    private BrandMapper brandMapper;

    //查询
    /**
     * 前台传回的参数
     *                   必传y   page:this.options.page,//分页
     *                   必传y   row:this.options.rowsPerPage,//一页显示多少条
     *                     searchKey:this.search,//搜索条件
     *                     sortBy:this.options.sortBy,//根据谁降序或升序
     *                     desc:this.options.descending//降序或升序
     *
     *                     defaultValue 默认填写的值
     *                     required :必须填写的属性,默认不写, 不默认时 required = false
     *                     */
    public PageResult<Brand> queryByList(Integer page, Integer row, String searchKey, String sortBy, Boolean desc) {
         //分页 : 使用的是分页助手启动器 pagehelper ;pom.xml引入的依赖
        //设置当前页和每页条数; limit第一个参数表示从该参数的下一条数据开始，第二个参数表示每次返回的数据条数。
        PageHelper.startPage(page,row);

        //过滤条件 前台搜索searchKey使用的模糊查询, 前台根据首字母sortBy排序
        //创建过滤条件类
        Example example =new Example(Brand.class);

        //判断前台搜索searchKey模糊查询
        if(searchKey != null && !searchKey.equals("")){
            //通过过滤条件类,构建实例,andLike交集        根据name名称,进行模糊查询
            //设置模糊查询
            example.createCriteria().andLike("name","%"+searchKey+"%");
        }

        //判断前台根据首字母sortBy排序
        if(sortBy != null && !sortBy.equals("")){
            //通过过滤条件类,设置排序
            example.setOrderByClause(sortBy+ (desc?" asc":" desc"));
        }
        //使用分页助手提供的类接收分页参数和实体参数
        Page<Brand>  pageData= (Page<Brand>) brandMapper.selectByExample(example);

        //最后返回,实体类brand和前台传回的参数,总条数total和当页显示的数据items
        return new PageResult<Brand>(pageData.getTotal(),pageData.getResult());
    }

    //删除
    public ResponseEntity findById(Long id) {
        try {
            if(id != null && id != 0){
                brandMapper.deleteByPrimaryKey(id);
                //删除分类 下的品牌
                brandMapper.deleteBrandById(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
           // throw new MrException(ExceptionEnums.DEL_CATEGORY_ERROR);
            throw new MrException(ExceptionEnums.ADD_UPDATE_CATEGORY_ERROR);
        }
        return ResponseEntity.ok("删除成功");
    }

    //增加品牌/修改品牌
    @Transactional //多表之间控制事务
    public void addBrand(Brand brand, List<Long> cids) {
        if (brand.getId() != null && brand.getId() != 0) {
            //修改品牌
            brandMapper.updateByPrimaryKey(brand);
            //修改品牌,中间表,品牌下的分类; 先删后增
            //删除原来的分类
            brandMapper.deleteBrandById(brand.getId());
            //后增
            for (Long cid : cids) {
                brandMapper.insertBrandCategory(brand.getId(), cid);
            }
        } else {

            //新增品牌
            brandMapper.insertSelective(brand);
            //新增品牌,中间表,品牌下的分类
            for (Long cid : cids) {
                brandMapper.insertBrandCategory(brand.getId(), cid);
            }

        }

    }

    //根据 分类 查询 品牌(用于spu商品修改回显)
    public List<Brand> queryCategoryBrand(Long cid) {
        return brandMapper.selectByKey(cid);
    }

    //用于es库,根据品牌id查询品牌
    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 用于es库,根据品牌ids,查询多个品牌数据(根据ids查询)
     */
    public List<Brand> queryBrandIdsList(List<Long> ids) {
        return brandMapper.selectByIdList(ids);
    }
}
