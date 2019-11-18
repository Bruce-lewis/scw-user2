package com.liuwei.scw.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.liuwei.scw.user.utils.SmsTemplate;

@Configuration
public class ScwUserAppConfig {
	// 属性值注入时 是通过sms前缀在properties文件中查找对应的属性，例如：sms.host ,
	// 再到getSmsTemplate方法返回的对象中查找 host属性，最后调用对象的.setHost(host的值);设置属性值
	@ConfigurationProperties(prefix="sms")
	@Bean
	public SmsTemplate getSmsTemplate() {
		SmsTemplate smsTemplate = new SmsTemplate();
		return smsTemplate;
	}
	
	@Bean
	public BCryptPasswordEncoder getBCryptPasswordEncoder() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
}
