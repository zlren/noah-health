package com.yhch.util;

import com.yhch.bean.CommonResult;
import com.yhch.controller.AuthController;
import org.slf4j.LoggerFactory;

/**
 * 短信验证码
 * Created by zlren on 2017/6/6.
 */
public class SMSUtil {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final static String HTTP_URL = "http://apis.baidu.com/kingtto_media/106sms/106sms";
    private final static String API_KEY = "xxxx";


    public static CommonResult send(String phone, String code, String content) throws Exception {

        if (!Validator.checkMobile(phone)) {
            return CommonResult.failure("手机号无效");
        }
        
        // String myUrl = HTTP_URL;
        // BufferedReader reader;
        // String result;
        // StringBuilder stringBuilder = new StringBuilder();
        //
        // String httpArg = "mobile=" + phone + "&content=" + URLEncoder.encode(content, "UTF-8") + "&tag=2";
        // myUrl = myUrl + "?" + httpArg;
        // URL url = new URL(myUrl);
        // HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // connection.setRequestMethod("GET");
        // connection.setRequestProperty("apikey", API_KEY);
        // connection.connect();
        // InputStream is = connection.getInputStream();
        // reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        // String strRead;
        // while ((strRead = reader.readLine()) != null) {
        //     stringBuilder.append(strRead);
        //     stringBuilder.append("\r\n");
        // }
        // reader.close();
        // result = stringBuilder.toString();

        // return result;
        return CommonResult.success("验证码发送成功");
    }
}
