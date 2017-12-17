package com.noahhealth.interceptor;


import com.noahhealth.bean.Constant;
import com.noahhealth.bean.Identity;
import com.noahhealth.service.PropertyService;
import com.noahhealth.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录认证拦截器（判断token有效性）
 * Created by ken on 2017/6/8.
 */
@Component
@Slf4j
public class TokenCertifyInterceptor implements HandlerInterceptor {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    private String getClientIp() {

        String remoteAddr = "";

        if (httpServletRequest != null) {
            remoteAddr = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = httpServletRequest.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {

        log.info("进入TokenCertifyInterceptor，ip地址为{}", getClientIp());

        // 验证token的有效性
        try {

            String token = request.getHeader("TOKEN");
            log.info("获得的token： {}", token);

            Identity identity = TokenUtil.parseToken(token, propertyService.apiKeySecret);

            // 把identity存入session中(其中包含用户名、角色、过期时间戳等)
            // request.setAttribute();
            request.getSession().setAttribute(Constant.IDENTITY, identity);

            log.info("{}: token有效", identity.getUsername());

            return true;
        } catch (Exception e) {

            log.info("TOKEN无效，转到登录界面");
            response.sendRedirect("/api/auth/login_denied");

            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView
            modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception
            ex) throws Exception {

    }
}
