package com.yhch.controller;


import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.PageResult;
import com.yhch.bean.user.UserExtend;
import com.yhch.pojo.ResultHealth;
import com.yhch.pojo.User;
import com.yhch.service.ResultHealthService;
import com.yhch.service.UserService;
import com.yhch.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping("health")
@RestController
public class ResultHealthController {

    @Autowired
    private ResultHealthService resultHealthService;

    @Autowired
    private UserService userService;


    /**
     * 添加一条健康摘要
     *
     * @param params
     * @param session
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public CommonResult addResultHealthRecord(@RequestBody Map<String, Object> params, HttpSession session) {
        Integer userId = (Integer) params.get("userId");

        {
            User record = new User();
            record.setId(userId);
            if (this.userService.queryOne(record).getRole().equals(Constant.USER_1)) {
                // 1级用户没有此权限
                return CommonResult.failure("一级用户无此权限");
            }
        }

        Integer secondId = (Integer) params.get("secondId");
        Integer inputerId = Integer.valueOf(((Identity) session.getAttribute(Constant.IDENTITY)).getId());
        String status = Constant.LU_RU_ZHONG; // 初始状态为录入中
        String note = (String) params.get(Constant.NOTE);
        Date time = TimeUtil.parseTime((String) params.get(Constant.TIME));

        ResultHealth resultHealth = new ResultHealth();
        resultHealth.setUserId(userId);
        resultHealth.setSecondId(secondId);
        resultHealth.setInputerId(inputerId);
        resultHealth.setStatus(status);
        resultHealth.setNote(note);

        resultHealth.setTime(time);
        resultHealth.setUploadTime(TimeUtil.getCurrentTime());

        this.resultHealthService.save(resultHealth);

        return CommonResult.success("添加成功");
    }


    /**
     * 显示用户列表，实际上和input那里是一致的
     *
     * @param params
     * @param session
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    public CommonResult queryResultHealthUserList(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);
        String userName = (String) params.get("userName");
        String memberNum = (String) params.get("memberNum");
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<User> userList = this.resultHealthService.queryResultInputUserList(identity, userName, memberNum,
                pageNow, pageSize);
        PageResult pageResult = new PageResult(new PageInfo<>(userList));

        List<UserExtend> userExtendList = this.userService.extendFromUser(userList);
        pageResult.setData(userExtendList);

        return CommonResult.success("查询成功", pageResult);
    }


    /**
     * 更新健康摘要
     *
     * @param healthId
     * @param params
     * @return
     */
    @RequestMapping(value = "{healthId}", method = RequestMethod.PUT)
    public CommonResult updateResultHealthValue(@PathVariable("healthId") Integer healthId, @RequestBody Map<String,
            String> params) {

        String value = params.get("new");

        ResultHealth resultHealth = new ResultHealth();
        resultHealth.setId(healthId);
        resultHealth.setValueNew(value);

        this.resultHealthService.updateSelective(resultHealth);

        return CommonResult.success("更新成功");
    }


    /**
     * 改变状态
     *
     * @param inputId
     * @param params
     * @param session
     * @return
     */
    @RequestMapping(value = "status/{inputId}", method = RequestMethod.PUT)
    public CommonResult submitOriginRecord(@PathVariable("inputId") Integer inputId, @RequestBody Map<String, Object>
            params, HttpSession session) {

    }

}
