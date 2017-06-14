package com.yhch.service;

import com.github.pagehelper.PageHelper;
import com.yhch.bean.CommonResult;
import com.yhch.bean.Constant;
import com.yhch.bean.Identity;
import com.yhch.pojo.User;
import com.yhch.util.TokenUtil;
import com.yhch.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserService extends BaseService<User> {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * 检查用户名是否重复
     *
     * @param username
     * @return true 重复（数据库中存在）
     */
    public boolean isExist(String username) {
        User record = new User();
        record.setUsername(username);
        return super.queryOne(record) != null;
    }


    /**
     * 为通过登录验证的用户生成token
     *
     * @param id
     * @param issuer
     * @param phone
     * @param role
     * @param duration
     * @param apiKeySecret
     * @return
     */
    public CommonResult generateToken(String id, String issuer, String phone, String role, Long duration, String
            apiKeySecret) {

        Identity identity = new Identity();
        identity.setId(id);
        identity.setIssuer(issuer);
        identity.setPhone(phone);
        identity.setRole(role);
        identity.setDuration(duration);
        String token = TokenUtil.createToken(identity, apiKeySecret);

        // 封装返回前端(除了用户名、角色、时间戳保留，其余消去)
        identity.setToken(token);
        // identity.setId(id);
        identity.setIssuer(null);
        return CommonResult.success("登录成功", identity);
    }


    /**
     * 条件查询会员
     *
     * @param pageNow
     * @param pageSize
     * @param role
     * @param phone
     * @param name
     * @return
     */
    public List<User> queryUserList(Integer pageNow, Integer pageSize, String role, String phone, String name, String
            type) {

        logger.info("pageNow: {}, pageSize: {}, role: {}, phone: {}, name: {}", pageNow, pageSize, role, phone, name);

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();

        if (!Validator.checkEmpty(name)) {
            criteria.andLike(Constant.NAME, "%" + name + "%");
        }

        if (!Validator.checkEmpty(phone)) {
            criteria.andLike(Constant.PHONE, "%" + phone + "%");
        }

        if (!Validator.checkEmpty(role)) {
            criteria.andLike(Constant.ROLE, "%" + role + "%");
        } else {
            if (type.equals(Constant.MEMBER)) {
                criteria.andLike(Constant.ROLE, "%" + "会员" + "%");
            } else { // type.equals("employee")
                criteria.andNotLike(Constant.ROLE, "%" + "会员" + "%");
            }
        }

        PageHelper.startPage(pageNow, pageSize);
        return this.getMapper().selectByExample(example);
    }

}
