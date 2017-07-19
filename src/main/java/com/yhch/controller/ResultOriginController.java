package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.bean.PageResult;
import com.yhch.bean.origin.ResultOriginExtend;
import com.yhch.pojo.ResultOrigin;
import com.yhch.pojo.User;
import com.yhch.service.PropertyService;
import com.yhch.service.ResultOriginService;
import com.yhch.service.UserService;
import com.yhch.util.TimeUtil;
import com.yhch.util.Validator;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 原始数据管理
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
    private PropertyService propertyService;


    /**
     * 上传扫描件
     *
     * @param file
     * @param id
     * @return
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult addResultOriginFile(@RequestParam("file") MultipartFile file, Integer id) {

        ResultOrigin resultOrigin = this.resultOriginService.queryById(id);
        if (resultOrigin == null) {
            return CommonResult.failure("上传失败，记录不存在");
        }

        String fileName;

        if (!file.isEmpty()) {

            String originFileName = file.getOriginalFilename();

            if (originFileName.contains("_") || originFileName.contains("/")) {
                return CommonResult.failure("上传失败，文件名包含特殊字符");
            }

            String preFileName = id + "_" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4);
            fileName = preFileName + "_" + file.getOriginalFilename();

            try {
                Streams.copy(file.getInputStream(), new FileOutputStream(this.propertyService.filePath + "origin/" +
                                fileName),
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String path = resultOrigin.getPath();
            if (path == null || path.length() == 0) {
                resultOrigin.setPath(fileName);
            } else {
                resultOrigin.setPath(path + ";" + fileName);
            }

            this.resultOriginService.update(resultOrigin);
        } else {
            return CommonResult.failure("文件上传失败");
        }

        return CommonResult.success("文件上传成功", "/origin/" + fileName);
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
        String note = (String) params.get(Constant.NOTE);

        Date time;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd").parse((String) params.get(Constant.TIME));
        } catch (ParseException e) {
            e.printStackTrace();
            return CommonResult.failure("添加失败，日期解析错误");
        }

        ResultOrigin resultOrigin = new ResultOrigin();
        resultOrigin.setUserId(userId);
        resultOrigin.setTime(time);
        resultOrigin.setUploaderId(uploaderId);
        resultOrigin.setCheckerId(null);
        try {
            resultOrigin.setUploadTime(TimeUtil.getCurrentTime());
        } catch (ParseException ignored) {
            return CommonResult.failure("解析时间出错");
        }

        // 初始状态上传中
        resultOrigin.setStatus(Constant.SHANG_CHUAN_ZHONG);

        resultOrigin.setNote(note);

        this.resultOriginService.save(resultOrigin);

        return CommonResult.success("添加成功", resultOrigin.getId());
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

        if (this.resultOriginService.queryById(originId) == null) {
            return CommonResult.failure("删除失败，不存在的记录");
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
        Integer uploaderId = (Integer) params.get(Constant.UPLOADER_ID);

        if (userId != null) {
            resultOrigin.setUserId(userId);
        }

        if (uploaderId != null) {
            resultOrigin.setUploaderId(uploaderId);
        }

        this.resultOriginService.update(resultOrigin);

        return CommonResult.success("修改成功");
    }


    /**
     * 条件分页查询原始数据列表
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult queryResultOriginList(@RequestBody Map<String, Object> params, HttpSession session) {

        Integer pageNow = (Integer) params.get(Constant.PAGE_NOW);
        Integer pageSize = (Integer) params.get(Constant.PAGE_SIZE);

        String status = (String) params.get(Constant.STATUS);
        String userName = (String) params.get("userName");
        String uploaderName = (String) params.get("uploaderName");
        String checkerName = (String) params.get("checkerName");
        String memberNum = (String) params.get("memberNum");
        Date beginTime = TimeUtil.parseTime((String) params.get("beginTime"));
        Date endTime = TimeUtil.parseTime((String) params.get("endTime"));

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);

        List<ResultOrigin> resultOriginList = this.resultOriginService.queryResultOriginList(identity, pageNow,
                pageSize, status, userName, uploaderName, checkerName, memberNum, beginTime, endTime);


        PageResult pageResult = new PageResult(new PageInfo<>(resultOriginList));

        List<ResultOriginExtend> resultOriginExtendList = new ArrayList<>();

        resultOriginList.forEach(resultOrigin -> {

            String memberNumExtend = this.userService.queryById(resultOrigin.getUserId()).getMemberNum();
            String userNameExtend = this.userService.queryById(resultOrigin.getUserId()).getName();
            String checkerNameExtend = null;
            if (resultOrigin.getCheckerId() != null) {
                checkerNameExtend = this.userService.queryById(resultOrigin.getCheckerId()).getName();
            }
            String uploaderNameExtend = this.userService.queryById(resultOrigin.getUploaderId()).getName();

            ResultOriginExtend resultOriginExtend = new ResultOriginExtend(resultOrigin, memberNumExtend,
                    userNameExtend, checkerNameExtend, uploaderNameExtend);

            resultOriginExtendList.add(resultOriginExtend);
        });

        pageResult.setData(resultOriginExtendList);

        return CommonResult.success("查询成功", pageResult);
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
        List<User> users = this.userService.queryMemberListUnderEmployee(identity);
        return CommonResult.success("查询成功", users);
    }


    /**
     * 根据originId查找file信息
     *
     * @param originId
     * @return
     */
    @RequestMapping(value = "file/{originId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult queryFileListByRecordId(@PathVariable("originId") Integer originId) {

        ResultOrigin resultOrigin = this.resultOriginService.queryById(originId);
        if (resultOrigin == null) {
            return CommonResult.failure("查询失败，不存在的记录");
        }

        List<Map<String, String>> result = new ArrayList<>();

        String path = resultOrigin.getPath();

        if (!Validator.checkEmpty(path)) {
            String[] split = path.split(";");
            for (String s : split) {
                Map<String, String> map = new HashMap<>();
                map.put("name", s.split("_")[2]);
                map.put("url", "/origin/" + s);

                result.add(map);
            }
        }

        return CommonResult.success("查询成功", result);
    }


    /**
     * 根据originId查找file信息
     *
     * @param originId
     * @return
     */
    @RequestMapping(value = "file/{originId}", method = RequestMethod.DELETE)
    @ResponseBody
    public CommonResult queryFileListByRecordId(@PathVariable("originId") Integer originId, @RequestBody Map<String,
            Object> params) {

        ResultOrigin resultOrigin = this.resultOriginService.queryById(originId);
        if (resultOrigin == null) {
            return CommonResult.failure("删除失败，不存在的记录");
        }

        String fileName = (String) params.get("fileName");

        if (Validator.checkEmpty(fileName)) {
            return CommonResult.failure("删除失败，缺少参数");
        }

        String path = resultOrigin.getPath();
        StringBuilder updatePath = new StringBuilder();

        // 切开，遍历，组装
        if (!Validator.checkEmpty(path)) {
            String[] split = path.split(";");
            for (String s : split) {
                if (!s.equals(fileName)) {
                    if (updatePath.length() == 0) {
                        updatePath = new StringBuilder(s);
                    } else {
                        updatePath.append(";").append(s);
                    }
                }
            }
        }

        resultOrigin.setPath(updatePath.toString());
        this.resultOriginService.update(resultOrigin);

        return CommonResult.success("删除成功");
    }

    /**
     * 更改状态
     *
     * @param originId
     * @return
     */
    @RequestMapping(value = "status/{originId}", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResult submitOriginRecord(@PathVariable("originId") Integer originId,
                                           @RequestBody Map<String, Object> params, HttpSession session) {

        ResultOrigin resultOrigin = this.resultOriginService.queryById(originId);
        if (resultOrigin == null) {
            return CommonResult.failure("提交失败，不存在的记录");
        }

        // 待审核（提交）、已通过、未通过（附加reason）
        String status = (String) params.get(Constant.STATUS);
        String reason = (String) params.get(Constant.REASON);

        if (Validator.checkEmpty(status)) {
            return CommonResult.failure("修改失败，缺少参数");
        }

        // checker
        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        Integer checkerId = Integer.valueOf(identity.getId());

        if (status.equals(Constant.DAI_SHEN_HE)) { // 提交，待审核

            if (Validator.checkEmpty(resultOrigin.getPath())) {
                return CommonResult.failure("提交失败，请上传至少一份文件");
            }

            // 状态改为'待审核'
            resultOrigin.setStatus(Constant.DAI_SHEN_HE);
            this.resultOriginService.update(resultOrigin);

            return CommonResult.success("提交成功");

        } else if (status.equals(Constant.WEI_TONG_GUO)) { // 未通过

            if (Validator.checkEmpty(reason)) {
                reason = "<未说明原因>";
            }

            resultOrigin.setCheckerId(checkerId);
            resultOrigin.setStatus(Constant.WEI_TONG_GUO);
            resultOrigin.setReason(reason);
            this.resultOriginService.update(resultOrigin);

            return CommonResult.success("操作成功");

        } else if (status.equals(Constant.YI_TONG_GUO)) { // 通过，已通过

            resultOrigin.setCheckerId(checkerId);
            resultOrigin.setStatus(Constant.YI_TONG_GUO);
            this.resultOriginService.update(resultOrigin);

            return CommonResult.success("操作成功");
        } else {
            return CommonResult.failure("参数错误");
        }
    }
}
