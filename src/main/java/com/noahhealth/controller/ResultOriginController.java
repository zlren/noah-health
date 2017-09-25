package com.noahhealth.controller;

import com.github.pagehelper.PageInfo;
import com.noahhealth.bean.CommonResult;
import com.noahhealth.bean.Constant;
import com.noahhealth.bean.Identity;
import com.noahhealth.bean.PageResult;
import com.noahhealth.bean.origin.ResultOriginExtend;
import com.noahhealth.pojo.ResultOrigin;
import com.noahhealth.pojo.ResultOriginFile;
import com.noahhealth.service.PropertyService;
import com.noahhealth.service.ResultOriginFileService;
import com.noahhealth.service.ResultOriginService;
import com.noahhealth.service.UserService;
import com.noahhealth.util.TimeUtil;
import com.noahhealth.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
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
@RestController
@Slf4j
public class ResultOriginController {

    @Autowired
    private ResultOriginService resultOriginService;

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private ResultOriginFileService resultOriginFileService;

    /**
     * 上传扫描件
     *
     * @param file
     * @param id
     * @return
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
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

            String preFileName = id + "_" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
            fileName = preFileName + "_" + file.getOriginalFilename();

            try {
                Streams.copy(file.getInputStream(), new FileOutputStream(this.propertyService.filePath + "origin/" +
                                fileName),
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 记录保存在result_origin_file表中
            ResultOriginFile resultOriginFile = new ResultOriginFile();
            resultOriginFile.setResultOriginId(id);
            resultOriginFile.setPath(fileName);

            this.resultOriginFileService.save(resultOriginFile);

        } else {
            return CommonResult.failure("文件上传失败");
        }

        return CommonResult.success("文件上传成功", "/origin/" + fileName);
    }


    /**
     * 添加原始资料记录
     *
     * @param session
     * @param params
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public CommonResult addResultOriginRecord(HttpSession session, @RequestBody Map<String, Object> params) {

        Identity identity = (Identity) session.getAttribute(Constant.IDENTITY);
        Integer uploaderId = Integer.valueOf(identity.getId());

        Integer userId = (Integer) params.get(Constant.USER_ID);
        String note = (String) params.get(Constant.NOTE);

        String hospital = (String) params.get("hospital");
        Integer secondId = (Integer) params.get("secondId");

        Date time;
        try {
            time = new SimpleDateFormat("yyyy-MM-dd").parse((String) params.get(Constant.TIME));
        } catch (ParseException e) {
            e.printStackTrace();
            return CommonResult.failure("添加失败，日期解析错误");
        }

        String normal = (String) params.get("normal");


        ResultOrigin resultOrigin = new ResultOrigin();
        resultOrigin.setUserId(userId);
        resultOrigin.setTime(time);
        resultOrigin.setUploaderId(uploaderId);
        resultOrigin.setCheckerId(null);
        resultOrigin.setSecondId(secondId); // 原始资料亚类
        resultOrigin.setHospital(hospital); // 原始资料医院
        resultOrigin.setUploadTime(TimeUtil.getCurrentTime());

        resultOrigin.setNormal(normal); // 新增的

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
    public CommonResult deleteResultOrigin(@PathVariable("originId") Integer originId) {

        if (this.resultOriginService.queryById(originId) == null) {
            return CommonResult.failure("删除失败，不存在的记录");
        }

        // 删除所有保存的文件路径
        ResultOriginFile record = new ResultOriginFile();
        record.setResultOriginId(originId);
        this.resultOriginFileService.deleteByWhere(record);

        this.resultOriginService.deleteById(originId);
        return CommonResult.success("删除成功");
    }


    /**
     * 条件分页查询原始数据列表
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.POST)
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

        // 过期的用户看不了
        if (!this.userService.checkValid(identity.getId())) {
            return CommonResult.failure("过期无效的用户");
        }

        List<ResultOrigin> resultOriginList = this.resultOriginService.queryResultOriginList(null, identity, pageNow,
                pageSize, status, userName, uploaderName, checkerName, memberNum, beginTime, endTime);


        PageResult pageResult = new PageResult(new PageInfo<>(resultOriginList));


        List<ResultOriginExtend> resultOriginExtendList = this.resultOriginService.extendFromResultOriginList
                (resultOriginList);

        pageResult.setData(resultOriginExtendList);

        return CommonResult.success("查询成功", pageResult);
    }


    /**
     * 根据userId查询原始数据列表
     *
     * @param params
     * @param session
     * @return
     */
    @RequestMapping(value = "list/{userId}", method = RequestMethod.POST)
    public CommonResult queryResultOriginListByUserId(@RequestBody Map<String, Object> params, HttpSession session,
                                                      @PathVariable("userId") Integer userId) {

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

        // 过期的用户看不了
        if (!this.userService.checkValid(identity.getId())) {
            return CommonResult.failure("过期无效的用户");
        }

        List<ResultOrigin> resultOriginList = this.resultOriginService.queryResultOriginList(userId, identity, pageNow,
                pageSize, status, userName, uploaderName, checkerName, memberNum, beginTime, endTime);
        PageResult pageResult = new PageResult(new PageInfo<>(resultOriginList));


        List<ResultOriginExtend> resultOriginExtendList = this.resultOriginService.extendFromResultOriginList
                (resultOriginList);
        pageResult.setData(resultOriginExtendList);

        return CommonResult.success("查询成功", pageResult);

    }


    /**
     * 根据originId查找file信息
     *
     * @param originId
     * @return
     */
    @RequestMapping(value = "file/{originId}", method = RequestMethod.GET)
    public CommonResult queryFileListByRecordId(@PathVariable("originId") Integer originId) {

        ResultOrigin resultOrigin = this.resultOriginService.queryById(originId);
        if (resultOrigin == null) {
            return CommonResult.failure("查询失败，不存在的记录");
        }

        List<Map<String, String>> result = new ArrayList<>();

        ResultOriginFile record = new ResultOriginFile();
        record.setResultOriginId(originId);
        List<ResultOriginFile> resultOriginFileList = this.resultOriginFileService.queryListByWhere(record);

        resultOriginFileList.forEach(filePath -> {
            Map<String, String> map = new HashMap<>();
            map.put("name", filePath.getPath().split("_")[2]);
            map.put("url", "/origin/" + filePath.getPath());
            result.add(map);
        });

        return CommonResult.success("查询成功", result);
    }


    /**
     * 删除文件
     *
     * @param originId
     * @return
     */
    @RequestMapping(value = "file/{originId}", method = RequestMethod.DELETE)
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

        ResultOriginFile record = new ResultOriginFile();
        record.setResultOriginId(originId);
        record.setPath(fileName);

        this.resultOriginFileService.deleteByWhere(record);

        return CommonResult.success("删除成功");
    }

    /**
     * 更改状态
     *
     * @param originId
     * @return
     */
    @RequestMapping(value = "status/{originId}", method = RequestMethod.PUT)
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
        String identityRole = identity.getRole();

        if (status.equals(Constant.DAI_SHEN_HE)) { // 提交，待审核

            ResultOriginFile record = new ResultOriginFile();
            record.setResultOriginId(originId);
            if (this.resultOriginFileService.queryCountByWhere(record) == 0) {
                return CommonResult.failure("提交失败，请上传至少一份文件");
            }

            // 状态改为'待审核'
            resultOrigin.setStatus(Constant.DAI_SHEN_HE);
            this.resultOriginService.update(resultOrigin);

            return CommonResult.success("提交成功");

        } else if (status.equals(Constant.WEI_TONG_GUO)) { // 未通过

            // 具有通过和未通过两项权利的人只有主管和ADMIN
            if (!this.userService.checkManager(identityRole) && !this.userService.checkAdmin(identityRole)) {
                return CommonResult.failure("无此权限");
            }

            if (Validator.checkEmpty(reason)) {
                reason = "<未说明原因>";
            }

            resultOrigin.setCheckerId(checkerId);
            resultOrigin.setStatus(Constant.WEI_TONG_GUO);
            resultOrigin.setReason(reason);
            this.resultOriginService.update(resultOrigin);

            return CommonResult.success("操作成功");

        } else if (status.equals(Constant.YI_TONG_GUO)) { // 通过，已通过

            // 具有通过和未通过两项权利的人只有主管和ADMIN
            if (!this.userService.checkManager(identityRole) && !this.userService.checkAdmin(identityRole)) {
                return CommonResult.failure("无此权限");
            }

            resultOrigin.setCheckerId(checkerId);
            resultOrigin.setStatus(Constant.YI_TONG_GUO);
            this.resultOriginService.update(resultOrigin);

            return CommonResult.success("操作成功");
        } else {
            return CommonResult.failure("参数错误");
        }
    }
}
