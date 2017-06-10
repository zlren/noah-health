package com.yhch.service;

import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 权限检查
 * Created by zlren on 2017/6/10.
 */
@Service
public class AuthorityService {
    public CommonResult check(HttpSession session, List<String> roles) {
        String role = ((Identity) session.getAttribute(Constant.IDENTITY)).getRole();
        for (String s : roles) {
            if (s.equals(role)) {
                return CommonResult.success("权限通过", null);
            }
        }
        return CommonResult.failure("无此权限");
    }
}
