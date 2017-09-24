package com.noahhealth.util;

import com.noahhealth.bean.Identity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT生成与验证token
 * Created by ken on 2017/6/8.
 */
public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    /**
     * 生成token
     *
     * @param identity
     * @param apiKeySecret
     * @return
     */
    public static String createToken(Identity identity, String apiKeySecret) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKeySecret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", identity.getUsername());
        claims.put("role", identity.getRole());
        claims.put("id", identity.getId());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setId(identity.getId())
                .setIssuedAt(now)
                .setIssuer(identity.getIssuer())
                .signWith(signatureAlgorithm, signingKey);

        //if it has been specified, let's add the expiration
        long ttlMillis = identity.getDuration();
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
            identity.setDuration(exp.getTime());
        }

        //Builds the JWT and serializes it to a compact, URL-safe string
        logger.info("TOKEN生成成功");
        return builder.compact();
    }

    /**
     * 解析token
     *
     * @param token
     * @param apiKeySecret
     * @return
     * @throws Exception
     */
    public static Identity parseToken(String token, String apiKeySecret) throws Exception {

        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(apiKeySecret))
                .parseClaimsJws(token).getBody();

        String id = (String) claims.get("id");
        String username = (String) claims.get("username");
        String role = (String) claims.get("role");

        // 封装成pojo
        Identity identity = new Identity();
        identity.setId(id);
        identity.setUsername(username);
        identity.setRole(role);
        identity.setDuration(claims.getExpiration().getTime());

        logger.info("已登录的用户，有效token");
        return identity;
    }
}
