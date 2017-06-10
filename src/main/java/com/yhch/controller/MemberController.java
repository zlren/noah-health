package com.yhch.controller;

import com.yhch.bean.CommonResult;
import com.yhch.service.MemberService;
import com.yhch.service.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * MemberControllerAdmin
 */
@Controller
@RequestMapping("/member")
public class MemberController {


    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private PropertyService propertyService;


    @RequestMapping("/search/{member_id}")
    @ResponseBody
    public CommonResult searchMemberById(@PathVariable("member_id") Integer memberId, HttpSession session) {

        logger.info("进入searchMemberById.action");

        return CommonResult.success("已登录", memberService.queryById(memberId));

    }

    /**
     * 根据会员id删除会员
     *
     * @param memberId
     * @return
     */
    @RequestMapping("/delete/{member_id}")
    public void deleteMember(@PathVariable("member_id") Integer memberId) {
        memberService.deleteById(memberId);
    }
}
