package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.rolecheck.RequiredRoles;
import com.yhch.pojo.CategoryFirst;
import com.yhch.pojo.CategorySecond;
import com.yhch.service.CategoryFirstService;
import com.yhch.service.CategorySecondService;
import com.yhch.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private PropertyService propertyService;


    /**
     * 添加一个大类
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @RequiredRoles(roles = {"ADMIN"})
    public CommonResult addCategoryFist(@RequestBody Map<String, String> params) {

        String type = params.get(Constant.TYPE);
        String name = params.get(Constant.NAME);

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
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    @RequiredRoles(roles = {"ADMIN"})
    public CommonResult deleteCategoryFist(@RequestBody Map<String, Integer> params) {

        Integer id = params.get(Constant.ID);

        CategorySecond categorySecond = new CategorySecond();
        categorySecond.setFirstId(id);
        List<CategorySecond> categorySeconds = this.categorySecondService.queryListByWhere(categorySecond);
        if (categorySeconds != null && categorySeconds.size() > 0) {
            return CommonResult.failure("存在亚类，无法删除");
        }

        this.categoryFirstService.deleteById(id);
        return CommonResult.success("删除成功");
    }


    /**
     * 修改一个大类
     *
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @RequiredRoles(roles = {"ADMIN"})
    public CommonResult updateCategoryFist(@RequestBody Map<String, Object> params) {

        Integer id = (Integer) params.get(Constant.ID);
        String type = (String) params.get(Constant.TYPE);
        String name = (String) params.get(Constant.NAME);

        CategoryFirst categoryFirst = new CategoryFirst();
        categoryFirst.setId(id);
        categoryFirst.setType(type);
        categoryFirst.setName(name);

        this.categoryFirstService.update(categoryFirst);
        return CommonResult.success("修改成功");
    }


    /**
     * 分页查询某个type（医技、化验）下的大类
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list_first", method = RequestMethod.GET)
    @ResponseBody
    // @RoleCheck(roles = {"ADMIN"})
    public CommonResult queryListFirst(@RequestBody Map<String, Object> params) {

        String type = (String) params.get(Constant.TYPE);
        Integer pageCurrent = (Integer) params.get(Constant.PAGE_NOW);

        CategoryFirst categoryFirst = new CategoryFirst();
        categoryFirst.setType(type);

        PageInfo<CategoryFirst> categoryFirstPageInfo = this.categoryFirstService.queryPageListByWhere
                (pageCurrent, propertyService.pageRows, categoryFirst);
        return CommonResult.success("查询成功", categoryFirstPageInfo);
    }


    /**
     * 分页查询大类下面的所有亚类
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list_second", method = RequestMethod.GET)
    @ResponseBody
    // @RoleCheck(roles = {"ADMIN"})
    public CommonResult queryListSecond(@RequestBody Map<String, Object> params) {

        Integer id = (Integer) params.get(Constant.ID);
        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);

        CategorySecond categorySecond = new CategorySecond();
        categorySecond.setFirstId(id);

        PageInfo<CategorySecond> categorySecondPageInfo = this.categorySecondService.queryPageListByWhere
                (pageNow, propertyService.pageRows, categorySecond);
        return CommonResult.success("查询成功", categorySecondPageInfo);
    }
}
