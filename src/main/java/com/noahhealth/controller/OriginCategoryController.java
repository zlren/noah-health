package com.noahhealth.controller;

import com.github.pagehelper.PageInfo;
import com.noahhealth.bean.CommonResult;
import com.noahhealth.bean.Constant;
import com.noahhealth.pojo.OriginCategoryFirst;
import com.noahhealth.pojo.OriginCategorySecond;
import com.noahhealth.service.OriginCategoryFirstService;
import com.noahhealth.service.OriginCategorySecondService;
import com.noahhealth.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zlren on 17/7/19.
 */
@RestController
@RequestMapping("origin_category")
@Slf4j
public class OriginCategoryController {

    @Autowired
    private OriginCategoryFirstService originCategoryFirstService;

    @Autowired
    private OriginCategorySecondService originCategorySecondService;


    /**
     * 添加原始资料分类的大类
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "first", method = RequestMethod.POST)
    public CommonResult addOriginCategoryFirst(@RequestBody Map<String, String> params) {
        String originCategoryFirstName = params.get("name");

        if (Validator.checkEmpty(originCategoryFirstName)) {
            return CommonResult.failure("添加失败，缺少参数");
        }

        OriginCategoryFirst record = new OriginCategoryFirst();
        record.setName(originCategoryFirstName);
        this.originCategoryFirstService.save(record);

        return CommonResult.success("添加成功");
    }


    /**
     * 添加原始资料分类的亚类
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "second", method = RequestMethod.POST)
    public CommonResult addOriginCategorySecond(@RequestBody Map<String, Object> params) {

        Integer first = (Integer) params.get("firstId");
        String secondName = (String) params.get("name");

        if (first == null || Validator.checkEmpty(secondName)) {
            return CommonResult.failure("添加失败，缺少参数");
        }

        OriginCategorySecond record = new OriginCategorySecond();
        record.setFirstId(first);
        record.setName(secondName);
        this.originCategorySecondService.save(record);

        return CommonResult.success("添加成功");
    }


    /**
     * 分级查询
     *
     * @return
     */
    @RequestMapping(value = "level", method = RequestMethod.GET)
    public CommonResult queryOriginFirstSecondLevel() {

        Map<String, List<OriginCategorySecond>> result = new HashMap<>();

        List<OriginCategoryFirst> originCategoryFirstList = this.originCategoryFirstService.queryListByWhere(null);

        originCategoryFirstList.forEach(originCategoryFirst -> {

            OriginCategorySecond originCategorySecond = new OriginCategorySecond();
            originCategorySecond.setFirstId(originCategoryFirst.getId());

            List<OriginCategorySecond> originCategorySecondList = this.originCategorySecondService.queryListByWhere(originCategorySecond);

            // 如果一个大类不存在亚类，不把它加入到level中
            if (originCategorySecondList != null && originCategorySecondList.size() > 0) {
                result.put(originCategoryFirst.getName(), originCategorySecondList);
            }
        });

        return CommonResult.success("查询成功", result);
    }


    /**
     * 删除一个资料大类
     *
     * @param firstId
     * @return
     */
    @RequestMapping(value = "first/{firstId}", method = RequestMethod.DELETE)
    public CommonResult deleteOriginCategoryFist(@PathVariable("firstId") Integer firstId) {

        OriginCategorySecond originCategorySecond = new OriginCategorySecond();
        originCategorySecond.setFirstId(firstId);
        Integer count = this.originCategorySecondService.queryCountByWhere(originCategorySecond);

        if (count > 0) {
            return CommonResult.failure("存在资料亚类，无法删除");
        }

        if (this.originCategoryFirstService.queryById(firstId) == null) {
            return CommonResult.failure("删除失败，不存在的资料主类");
        }

        this.originCategoryFirstService.deleteById(firstId);
        return CommonResult.success("删除成功");
    }


    /**
     * 查询原始数据大类列表
     *
     * @return
     */
    @RequestMapping(value = "first/list", method = RequestMethod.GET)
    public CommonResult queryOriginCategoryFirstList() {
        return CommonResult.success("查询成功", this.originCategoryFirstService.queryAll());
    }


    /**
     * 根据firstId分页查询亚类
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "second/{firstId}/list", method = RequestMethod.POST)
    public CommonResult queryOriginCategorySecondListByFirstId(@RequestBody Map<String, Integer> params, @PathVariable("firstId") Integer firstId) {

        Integer pageNow = params.get(Constant.PAGE_NOW);
        Integer pageSize = params.get(Constant.PAGE_SIZE);

        OriginCategorySecond record = new OriginCategorySecond();
        record.setFirstId(firstId);
        PageInfo<OriginCategorySecond> originCategorySecondPageInfo = this.originCategorySecondService.queryPageListByWhere(pageNow, pageSize, record);

        return CommonResult.success("查询成功", originCategorySecondPageInfo);
    }


    /**
     * 删除一个资料亚类
     *
     * @param secondId
     * @return
     */
    @RequestMapping(value = "second/{secondId}", method = RequestMethod.DELETE)
    public CommonResult deleteOriginCategorySecond(@PathVariable("secondId") Integer secondId) {

        if (this.originCategorySecondService.queryById(secondId) == null) {
            return CommonResult.failure("删除失败，不存在的资料亚类");
        }

        this.originCategorySecondService.deleteById(secondId);
        return CommonResult.success("删除成功");
    }

}
