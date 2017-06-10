package com.yhch.bean.control;

import java.lang.annotation.*;

/**
 * 自定义注解
 * Created by zlren on 17/6/10.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoleCheck {
    // List<String> roles() default ;
    String[] roles() default {};
}
