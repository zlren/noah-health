package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.PageResult;
import com.yhch.pojo.ResultOrigin;
import com.yhch.pojo.User;
import com.yhch.service.CategorySecondService;
import com.yhch.service.ResultOriginService;
import com.yhch.service.UserService;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 原始数据
 * Created by zlren on 2017/6/21.
 */
@RequestMapping("origin")
@Controller
public class ResultOriginController {

    private static final Logger logger = LoggerFactory.getLogger(ResultOriginController.class);

    @Autowired
    private ResultOriginService resultOriginService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategorySecondService categorySecondService;

    /**
     * 上传扫描件
     *
     * @param files
     * @param request
     * @return
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addResultOriginFile(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) {

        for (MultipartFile multipartFile : files) {
            if (!multipartFile.isEmpty()) {
                try {
                    Streams.copy(multipartFile.getInputStream(),
                            new FileOutputStream(
                                    "/root/yhch-resources/origin/" + "1" + "_" + multipartFile.getOriginalFilename()),
                            true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return CommonResult.success("文件上传成功");
    }


    /**
     * 上传记录
     *
     * @param session
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addResultOriginRecord(HttpSession session, @RequestBody Map<String, Object> params) {

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        Integer uploaderId = Integer.valueOf(identity.getId());


        Integer userId = (Integer) params.get(Constant.USER_ID);
        Integer secondId = (Integer) params.get(Constant.SECOND_ID);

        Date time;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd").parse((String) params.get(Constant.TIME));
        } catch (ParseException e) {
            e.printStackTrace();
            return CommonResult.failure("添加失败，日期解析错误");
        }

        ResultOrigin resultOrigin = new ResultOrigin();
        resultOrigin.setUserId(userId);
        resultOrigin.setUserName(this.userService.queryById(userId).getName());
        resultOrigin.setSecondId(secondId);
        resultOrigin.setSecondName(this.categorySecondService.queryById(secondId).getName());
        resultOrigin.setTime(time);

        resultOrigin.setUploaderId(uploaderId);
        resultOrigin.setUploaderName(this.userService.queryById(uploaderId).getName());
        resultOrigin.setCheckerId(null);
        resultOrigin.setCheckerName(null);
        resultOrigin.setStatus(Constant.DAI_SHEN_HE);
        resultOrigin.setInputerId(null);
        resultOrigin.setInputerName(null);

        this.resultOriginService.save(resultOrigin);

        return CommonResult.success("添加成功");
    }


    /**
     * 删除原始数据
     *
     * @param originId
     * @return
     */
    @RequestMapping(value = "{originId}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult deleteResultOrigin(@PathVariable("originId") Integer originId) {

        if (this.resultOriginService.queryById(originId) != null) {
            return CommonResult.failure("删除失败，不存在的文件");
        }

        this.resultOriginService.deleteById(originId);
        return CommonResult.success("删除成功");
    }


    /**
     * 修改原始数据
     *
     * @param originId
     * @param params
     * @return
     */
    @RequestMapping(value = "{originId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult updateResultOrigin(@PathVariable("originId") Integer originId, @RequestBody Map<String,
            Object> params) {

        ResultOrigin resultOrigin = this.resultOriginService.queryById(originId);

        if (resultOrigin == null) {
            return CommonResult.failure("不存在的文件");
        }

        Integer userId = (Integer) params.get(Constant.USER_ID);
        Integer secondId = (Integer) params.get(Constant.SECOND_ID);
        Integer uploaderId = (Integer) params.get(Constant.UPLOADER_ID);
        String isPass = (String) params.get(Constant.IS_PASS);
        String isInput = (String) params.get(Constant.IS_INPUT);

        if (userId != null) {
            resultOrigin.setUserId(userId);
        }
        if (secondId != null) {
            resultOrigin.setSecondId(secondId);
        }
        if (uploaderId != null) {
            resultOrigin.setUploaderId(uploaderId);
        }
        // if (isPass != null) {
        //     resultOrigin.setIsPass(isPass);
        // }
        // if (isInput != null) {
        //     resultOrigin.setIsInput(isInput);
        // }

        this.resultOriginService.update(resultOrigin);

        return CommonResult.success("修改成功");
    }


    /**
     * 条件分页查询通过或者未通过的列表
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult showPassOrNotResultOriginList(@RequestBody Map<String, Object> params, HttpServletRequest
            request) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        String status = (String) params.get("status");
        String userName = (String) params.get("userName");
        String secondName = (String) params.get("secondName");
        String uploaderName = (String) params.get("uploaderName");
        String checkerName = (String) params.get("checkerName");
        String inputerName = (String) params.get("inputerName");
        Date time = (Date) params.get("time");

        // 当前用户的role和id
        String role = ((Identity) request.getSession().getAttribute(Constant.IDENTITY)).getRole();
        String id = ((Identity) request.getSession().getAttribute(Constant.IDENTITY)).getId();

        logger.info("当前用户的角色是{}, id是{}", role, id);
        logger.info("时间是{}", time);

        List<ResultOrigin> resultOriginList = this.resultOriginService.queryOriginList(status, userName, secondName,
                uploaderName, checkerName, inputerName, time, pageNow, pageSize);

        return CommonResult.success("查询成功", new PageResult(new PageInfo<>(resultOriginList)));
    }


    /**
     * 根据职员查询旗下的member
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "member_under_employee", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryMemberUnderEmployee(HttpSession session) {

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        // 当前用户的role和id
        String role = identity.getRole();
        String id = identity.getId();

        List<User> users = null;

        if (role.equals(Constant.ARCHIVE_MANAGER) || role.equals(Constant.ARCHIVER) || role.equals(Constant.ADMIN)) {
            // 档案部员工或者主管，所有的会员
            users = this.userService.queryAllMembers();
        } else if (role.equals(Constant.ADVISE_MANAGER)) {
            users = this.userService.queryMembersByAdviseMgrId(Integer.valueOf(id));
        } else if (role.equals(Constant.ADVISER)) {
            users = this.userService.queryMembersByAdviseId(Integer.valueOf(id));
        }

        return CommonResult.success("查询成功", users);
    }


    /**
     * 根据userId查询对应的所有原始数据列表
     *
     * @param userId
     * @param params
     * @return
     */
    @RequestMapping(value = "user/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult showResultOriginByUser(@PathVariable("userId") Integer userId, @RequestBody Map<String,
            Object> params) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        ResultOrigin record = new ResultOrigin();
        record.setUserId(userId);

        PageInfo<ResultOrigin> resultOriginPageInfo = this.resultOriginService.queryPageListByWhere(pageNow,
                pageSize, record);

        return CommonResult.success("查询成功", new PageResult(resultOriginPageInfo));
    }
}
