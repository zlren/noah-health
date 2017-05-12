package com.yhch.service;

import com.yhch.pojo.Member;
import org.springframework.stereotype.Service;

/**
 * MemberService
 */
@Service
public class MemberService extends BaseService<Member> {

    /**
     * 自定义一个BaseService中没有的方法
     *
     * @param name
     * @return
     */
    public Integer getIdByName(String name) {
        Member record = new Member();
        record.setName("zlren");
        Member member = getMapper().selectOne(record);
        return member.getId();
    }
}
