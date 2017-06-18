package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.pojo.CategorySecond;
import com.yhch.service.CategorySecondService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * 亚类
 * Created by zlren on 2017/6/14.
 */
@Controller
@RequestMapping("second")
public class CategorySecondController {

    @Autowired
    private CategorySecondService categorySecondService;


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
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public CommonResult deleteCategorySecond(@RequestBody Map<String, Integer> params) {
        Integer id = params.get(Constant.ID);

        if (this.categorySecondService.queryById(id) == null) {
            return CommonResult.failure("删除失败，不存在的亚类");
        }

        this.categorySecondService.deleteById(id);
        return CommonResult.success("删除成功");
    }

}
