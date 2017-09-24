package com.noahhealth.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.noahhealth.bean.CommonResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信验证码工具类
 * Created by zlren on 2017/6/6.
 */
@Slf4j
public class SMSUtil {

    private static final String accessKeyId = "LTAI4uAz2DR2gLw0";
    private static final String accessKeySecret = "4XPs6rkIUlzRKnoGHkitVy1VtBl8jJ";

    private static final String product = "Dysmsapi";
    private static final String domain = "dysmsapi.aliyuncs.com";

    /**
     * 发送验证码
     *
     * @param phone
     * @param code
     * @return
     * @throws ClientException
     */
    public static CommonResult send(String phone, String code) throws ClientException {

        if (!Validator.checkMobile(phone)) {
            return CommonResult.failure("手机号无效");
        }

        try {

            SendSmsResponse response = sendSms(phone, code);
            Thread.sleep(1000);

            // 查明细
            if (response.getCode() != null && response.getCode().equals("OK")) {
                return CommonResult.success("验证码发送成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failure("验证码发送失败，请稍后重试");
        }

        return CommonResult.failure("验证码发送失败，请稍后重试");
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @param code
     * @return
     * @throws ClientException
     */
    private static SendSmsResponse sendSms(String phone, String code) throws ClientException {

        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phone);
        request.setSignName("我的宿舍");
        request.setTemplateCode("SMS_77355063");
        request.setTemplateParam("{\"code\":\"" + code + "\"}");

        return acsClient.getAcsResponse(request);
    }


    // private static QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {
    //
    //     // 可自助调整超时时间
    //     System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
    //     System.setProperty("sun.net.client.defaultReadTimeout", "10000");
    //
    //     // 初始化acsClient,暂不支持region化
    //     IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
    //     DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
    //     IAcsClient acsClient = new DefaultAcsClient(profile);
    //
    //     // 组装请求对象
    //     QuerySendDetailsRequest request = new QuerySendDetailsRequest();
    //     // 必填-号码
    //     request.setPhoneNumber("15000000000");
    //     // 可选-流水号
    //     request.setBizId(bizId);
    //     //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
    //     SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
    //     request.setSendDate(ft.format(new Date()));
    //     //必填-页大小
    //     request.setPageSize(10L);
    //     //必填-当前页码从1开始计数
    //     request.setCurrentPage(1L);
    //
    //     //hint 此处可能会抛出异常，注意catch
    //     QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
    //
    //     return querySendDetailsResponse;
    // }

}
