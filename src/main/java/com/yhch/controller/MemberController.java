package com.yhch.controller;

import com.yhch.pojo.Member;
import com.yhch.service.MemberService;
import com.yhch.service.PropertyService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MemberControllerAdmin
 */
@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PropertyService propertyService;

    /**
     * 根据id精确查找会员
     *
     * @param memberId
     * @return
     */
    @RequestMapping("/search/{member_id}")
    @ResponseBody
    // @RequiresRoles(logical = Logical.OR, value = {"ADMIN", "USER-1"})
    @RequiresRoles("ADMIN")
    public Member searchMemberById(@PathVariable("member_id") Integer memberId) {
        return memberService.queryById(memberId);
    }

    /**
     * 根据会员id删除会员
     *
     * @param memberId
     * @return
     */
    @RequestMapping("/delete/{member_id}")
    @RequiresRoles("ADMIN")
    public void deleteMember(@PathVariable("member_id") Integer memberId) {
        memberService.deleteById(memberId);
    }
}
