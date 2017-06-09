package com.yhch.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 配置文件service
 * Created by zlren on 2017/2/28.
 */
@Service
public class PropertyService {

    @Value("${config.rows}")
    public int rows;

    @Value("${register.codeLen}")
    public int codeLen;

    @Value("${token.issuer}")
    public String issuer;

    @Value("${token.duration}")
    public long tokenDuration;

    @Value("${token.apiKeySecret}")
    public String apiKeySecret;
}
