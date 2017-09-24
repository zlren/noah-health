package com.noahhealth.controller;

import com.noahhealth.bean.CommonResult;
import com.noahhealth.bean.Constant;
import com.noahhealth.pojo.CategoryFirst;
import com.noahhealth.pojo.CategorySecond;
import com.noahhealth.service.CategoryFirstService;
import com.noahhealth.service.CategorySecondService;
import com.noahhealth.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大类
 * Created by zlren on 2017/6/12.
 */
@RequestMapping("first")
@RestController
@Slf4j
public class CategoryFirstController {

    @Autowired
    private CategoryFirstService categoryFirstService;

    @Autowired
    private CategorySecondService categorySecondService;


    /**
     * 根据id查询
     *
     * @param firstId
     * @return
     */
    @RequestMapping(value = "{firstId}", method = RequestMethod.GET)
    public CommonResult queryCategoryFirstById(@PathVariable("firstId") Integer firstId) {

        if (this.categoryFirstService.queryById(firstId) == null) {
            return CommonResult.failure("查询失败，不存在的大类");
        }

        return CommonResult.success("查询成功", this.categoryFirstService.queryById(firstId));
    }

    /**
     * 添加一个大类
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    // @RequiredRoles(roles = {"系统管理员"})
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

        // 设置id，根据id去改
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
    public CommonResult queryFirstSecondLevel(@RequestBody Map<String, Object> params) {

        String type = (String) params.get(Constant.TYPE);

        Map<String, List<CategorySecond>> result = new HashMap<>();

        CategoryFirst categoryFirst = new CategoryFirst();
        categoryFirst.setType(type);

        List<CategoryFirst> categoryFirstList = this.categoryFirstService.queryListByWhere(categoryFirst);

        categoryFirstList.forEach(first -> {

            CategorySecond categorySecond = new CategorySecond();
            categorySecond.setFirstId(first.getId());

            List<CategorySecond> categorySeconds = this.categorySecondService.queryListByWhere(categorySecond);

            // 如果一个大类不存在亚类，不把它加入到level中
            if (categorySeconds != null && categorySeconds.size() > 0) {
                result.put(first.getName(), categorySeconds);
            }
        });

        return CommonResult.success("查询成功", result);
    }
}
