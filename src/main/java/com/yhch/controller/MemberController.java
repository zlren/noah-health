package com.yhch.controller;

import com.github.pagehelper.PageInfo;
import com.yhch.bean.CommonResult;
import com.yhch.bean.PageResult;
import com.yhch.bean.control.RoleCheck;
import com.yhch.pojo.Member;
import com.yhch.service.MemberService;
import com.yhch.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 *
 */
@Controller
@RequestMapping("member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private PropertyService propertyService;

    /**
     * 查询会员信息
     *
     * @param memberId
     * @return
     */
    @RequestMapping(value = "{member_id}", method = RequestMethod.GET)
    @ResponseBody
    @RoleCheck(roles = {"ADMIN", "USER_1"})
    public CommonResult searchById(@PathVariable("member_id") Integer memberId) {
        return CommonResult.success("已登录", memberService.queryById(memberId));
    }

    /**
     * 更新会员
     *
     * @param memberId
     * @param params
     * @return
     */
    @RequestMapping(value = "{member_id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateById(@PathVariable("member_id") Integer memberId, @RequestBody Map<String, Object> params) {
        return CommonResult.success("已登录", memberService.queryById(memberId));
    }

    /**
     * 删除会员
     *
     * @param memberId
     * @return
     */
    @RequestMapping(value = "{member_id}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable("member_id") Integer memberId) {
        memberService.deleteById(memberId);
    }


    /**
     * 分页查询所有记录
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public CommonResult searchByList(@RequestBody Map<String, Object> params) {
        int pageCurrent = (int) params.get("pageCurrent");
        PageInfo<Member> memberPageInfo = this.memberService.queryPageListByWhere(pageCurrent, propertyService.rows, null);
        return CommonResult.success("查询成功", new PageResult(memberPageInfo));
    }
}
