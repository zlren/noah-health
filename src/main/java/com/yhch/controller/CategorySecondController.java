package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.PageResult;
import com.yhch.pojo.CategorySecond;
import com.yhch.pojo.CategoryThird;
import com.yhch.service.CategorySecondService;
import com.yhch.service.CategoryThirdService;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 亚类
 * Created by zlren on 2017/6/14.
 */
@RequestMapping("second")
@Controller
public class CategorySecondController {

    private static final Logger logger = LoggerFactory.getLogger(CategorySecondController.class);

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
    @ResponseBody
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
    @ResponseBody
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
        return CommonResult.success("添加成功");
    }


    /**
     * 删除一个亚类
     *
     * @param secondId
     * @return
     */
    @RequestMapping(value = "{secondId}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult deleteCategorySecond(@PathVariable("secondId") Integer secondId) {

        CategoryThird categoryThird = new CategoryThird();
        categoryThird.setSecondId(secondId);
        List<CategoryThird> categoryThirds = this.categoryThirdService.queryListByWhere(categoryThird);

        if (categoryThirds != null && categoryThirds.size() > 0) {
            return CommonResult.failure("存在亚亚类，无法删除");
        }

        if (this.categorySecondService.queryById(secondId) == null) {
            return CommonResult.failure("删除失败，不存在的亚类");
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
    @ResponseBody
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
    @ResponseBody
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
