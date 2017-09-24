package com.noahhealth.controller;

import com.github.pagehelper.PageInfo;
import com.noahhealth.bean.CommonResult;
import com.noahhealth.bean.Constant;
import com.noahhealth.bean.PageResult;
import com.noahhealth.pojo.CategorySecond;
import com.noahhealth.pojo.CategoryThird;
import com.noahhealth.service.CategoryFirstService;
import com.noahhealth.service.CategorySecondService;
import com.noahhealth.service.CategoryThirdService;
import com.noahhealth.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 亚类
 * Created by zlren on 2017/6/14.
 */
@RequestMapping("second")
@RestController
@Slf4j
public class CategorySecondController {

    @Autowired
    private CategoryFirstService categoryFirstService;

    @Autowired
    private CategorySecondService categorySecondService;

    @Autowired
    private CategoryThirdService categoryThirdService;

    /**
     * 根据id查询
     *
     * @param secondId
     * @return
     */
    @RequestMapping(value = "{secondId}", method = RequestMethod.GET)
    public CommonResult queryCategorySecondById(@PathVariable("secondId") Integer secondId) {

        if (this.categorySecondService.queryById(secondId) == null) {
            return CommonResult.failure("查询失败，不存在的亚类");
        }

        return CommonResult.success("查询成功", this.categorySecondService.queryById(secondId));
    }


    /**
     * 添加一个亚类
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public CommonResult addCategorySecond(@RequestBody Map<String, Object> params) {

        Integer firstId = (Integer) params.get(Constant.FIRST_ID);
        String name = (String) params.get(Constant.NAME);

        if (firstId == null || Validator.checkEmpty(name)) {
            return CommonResult.failure("添加失败，信息不完整");
        }

        CategorySecond categorySecond = new CategorySecond();
        categorySecond.setFirstId(firstId);
        categorySecond.setName(name);

        if (this.categorySecondService.queryOne(categorySecond) != null) {
            return CommonResult.failure("添加失败，已经存在的亚类");
        }

        this.categorySecondService.save(categorySecond);

        // 医技没有检查项，同步在third表中插入一条
        if (this.categoryFirstService.queryById(firstId).getType().equals("医技")) {
            CategoryThird categoryThird = new CategoryThird();
            categoryThird.setSecondId(categorySecond.getId()); // id回填
            categoryThird.setName(name); // 名字和医技亚类同名
            this.categoryThirdService.save(categoryThird);
        }

        return CommonResult.success("添加成功");
    }


    /**
     * 删除一个亚类
     *
     * @param secondId
     * @return
     */
    @RequestMapping(value = "{secondId}", method = RequestMethod.DELETE)
    public CommonResult deleteCategorySecond(@PathVariable("secondId") Integer secondId) {

        CategorySecond categorySecond = this.categorySecondService.queryById(secondId);
        String type = this.categoryFirstService.queryById(categorySecond.getFirstId()).getType();

        CategoryThird categoryThird = new CategoryThird();
        categoryThird.setSecondId(secondId);
        Integer count = this.categoryThirdService.queryCountByWhere(categoryThird);

        if (type.equals("化验")) {
            if (count > 0) {
                return CommonResult.failure("存在检查项目，无法删除");
            }
        } else {
            // 医技项目删掉自动添加的检查项目表
            this.categoryThirdService.deleteByWhere(categoryThird);
        }

        this.categorySecondService.deleteById(secondId);
        return CommonResult.success("删除成功");
    }


    /**
     * 修改亚类
     *
     * @param params
     * @param secondId
     * @return
     */
    @RequestMapping(value = "{secondId}", method = RequestMethod.PUT)
    public CommonResult updateCategorySecond(@RequestBody Map<String, Object> params, @PathVariable("secondId")
            Integer secondId) {

        if (this.categorySecondService.queryById(secondId) == null) {
            return CommonResult.failure("修改失败，不存在的亚类");
        }

        Integer firstId = (Integer) params.get(Constant.FIRST_ID);
        String name = (String) params.get(Constant.NAME);

        CategorySecond categorySecond = new CategorySecond();
        if (firstId == null || Validator.checkEmpty(name)) {
            return CommonResult.failure("修改失败，信息不完整");
        }

        categorySecond.setFirstId(firstId);
        categorySecond.setName(name);

        if (this.categorySecondService.queryOne(categorySecond) != null) {
            return CommonResult.failure("修改失败，已经存在的亚类");
        }

        // 设置id，根据id去改
        categorySecond.setId(secondId);

        this.categorySecondService.update(categorySecond);

        return CommonResult.success("修改成功");
    }


    /**
     * 根据firstId分页查询亚类
     *
     * @param params
     * @param firstId
     * @return
     */
    @RequestMapping(value = "{firstId}/list", method = RequestMethod.POST)
    public CommonResult querySecondCategoryList(@RequestBody Map<String, Integer> params, @PathVariable("firstId")
            Integer firstId) {

        Integer pageNow = params.get(Constant.PAGE_NOW);
        Integer pageSize = params.get(Constant.PAGE_SIZE);

        CategorySecond categorySecond = new CategorySecond();
        categorySecond.setFirstId(firstId);

        PageInfo<CategorySecond> categorySecondPageInfo = this.categorySecondService.queryPageListByWhere(pageNow,
                pageSize, categorySecond);
        return CommonResult.success("查询成功", new PageResult(categorySecondPageInfo));
    }
}
