package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.pojo.CategoryFirst;
import com.yhch.pojo.CategorySecond;
import com.yhch.service.CategoryFirstService;
import com.yhch.service.CategorySecondService;
import com.yhch.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大类
 * Created by zlren on 2017/6/12.
 */
@RequestMapping("first")
@Controller
public class CategoryFirstController {

    @Autowired
    private CategoryFirstService categoryFirstService;

    @Autowired
    private CategorySecondService categorySecondService;

    /**
     * 添加一个大类
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    // @RequiredRoles(roles = {"系统管理员"})
    @ResponseBody
    public CommonResult addCategoryFist(@RequestBody Map<String, String> params) {

        String type = params.get(Constant.TYPE);
        String name = params.get(Constant.NAME);

        if (Validator.checkEmpty(type) || Validator.checkEmpty(name)) {
            return CommonResult.failure("添加失败，信息不完整");
        }

        CategoryFirst categoryFirst = new CategoryFirst();
        categoryFirst.setName(name);
        categoryFirst.setType(type);

        if (this.categoryFirstService.queryOne(categoryFirst) != null) {
            return CommonResult.failure("已经存在");
        }

        this.categoryFirstService.save(categoryFirst);
        return CommonResult.success("添加成功");
    }


    /**
     * 删除一个大类
     *
     * @param firstId
     * @return
     */
    @RequestMapping(value = "{firstId}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult deleteCategoryFist(@PathVariable("firstId") Integer firstId) {

        CategorySecond categorySecond = new CategorySecond();
        categorySecond.setFirstId(firstId);
        List<CategorySecond> categorySeconds = this.categorySecondService.queryListByWhere(categorySecond);

        if (categorySeconds != null && categorySeconds.size() > 0) {
            return CommonResult.failure("存在亚类，无法删除");
        }

        if (this.categoryFirstService.queryById(firstId) == null) {
            return CommonResult.failure("删除失败，不存在的大类");
        }

        this.categoryFirstService.deleteById(firstId);
        return CommonResult.success("删除成功");
    }


    /**
     * 修改一个大类
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "{firstId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateCategoryFist(@RequestBody Map<String, Object> params, @PathVariable("firstId") Integer
            firstId) {

        if (this.categoryFirstService.queryById(firstId) == null) {
            return CommonResult.failure("修改失败，不存在的大类");
        }

        String type = (String) params.get(Constant.TYPE);
        String name = (String) params.get(Constant.NAME);

        if (Validator.checkEmpty(type) || Validator.checkEmpty(name)) {
            return CommonResult.failure("添加失败，信息不完整");
        }

        CategoryFirst categoryFirst = new CategoryFirst();
        categoryFirst.setType(type);
        categoryFirst.setName(name);

        if (this.categoryFirstService.queryOne(categoryFirst) != null) {
            return CommonResult.failure("修改失败，和其他大类重复");
        }

        categoryFirst.setId(firstId);
        this.categoryFirstService.update(categoryFirst);

        return CommonResult.success("修改成功");
    }


    /**
     * 查询所有大类列表
     *
     * @param type
     * @return
     */
    @RequestMapping(value = "{type}/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryListFirst(@PathVariable("type") String type) {

        if (!type.equals(Constant.HUAYAN) && !type.equals(Constant.YIJI)) {
            return CommonResult.failure("不支持的大类");
        }

        CategoryFirst record = new CategoryFirst();
        record.setType(type);
        List<CategoryFirst> categoryFirstList = this.categoryFirstService.queryListByWhere(record);
        return CommonResult.success("查询成功", categoryFirstList);
    }


    /**
     * 分级查询
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "level", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryFirstSecondLevel(@RequestBody Map<String, Object> params) {

        String type = (String) params.get(Constant.TYPE);

        Map<String, List<CategorySecond>> result = new HashMap<>();

        CategoryFirst categoryFirst = new CategoryFirst();
        categoryFirst.setType(type);
        List<CategoryFirst> categoryFirsts = this.categoryFirstService.queryListByWhere(categoryFirst);

        String firstName;
        for (CategoryFirst first : categoryFirsts) {

            firstName = first.getName();

            result.put(firstName, new ArrayList<>());

            CategorySecond categorySecond = new CategorySecond();
            categorySecond.setFirstId(first.getId());
            List<CategorySecond> categorySeconds = this.categorySecondService.queryListByWhere(categorySecond);

            for (CategorySecond second : categorySeconds) {
                result.get(firstName).add(second);
            }
        }

        return CommonResult.success("查询成功", result);
    }
}
