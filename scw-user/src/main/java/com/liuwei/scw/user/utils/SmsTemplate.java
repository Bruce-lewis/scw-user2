package com.liuwei.scw.user.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;


//@Component
@Data
public class SmsTemplate {
	
//	@Value("${sms.host}")
//	String host;
//	@Value("${sms.path}")
//	String path;
//	@Value("${sms.method}")
//	String method;
//	@Value("${sms.appcode}")
//	String appcode;
	String host;
	String path;
	String method;
	String appcode;
	// "15566286945" "code:1234" "TP1711063"
	public boolean testSms(String phone,String code,String template) {

		Map<String, String> headers = new HashMap<String, String>();
		// 最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile",phone);
		querys.put("param","code:"+code);
		querys.put("tpl_id", template);
//		querys.put("mobile","15566286945");
//		querys.put("param","code:"+code);
//		querys.put("tpl_id","TP1711063");
		Map<String, String> bodys = new HashMap<String, String>();

		try {
			/**
			 * 重要提示如下: HttpUtils请从
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
			 * 下载
			 *
			 * 相应的依赖请参照
			 * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
			 */
			HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//			System.out.println(response.toString());
//			// 获取response的body
//			String str = EntityUtils.toString(response.getEntity());
//			System.out.println("短信发送后相应"+str);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
