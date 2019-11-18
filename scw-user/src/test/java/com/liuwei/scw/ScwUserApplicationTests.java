package com.liuwei.scw;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.liuwei.scw.user.utils.HttpUtils;
import com.liuwei.scw.user.utils.SmsTemplate;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ScwUserApplicationTests {

	Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	DataSource dataSource;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	RedisTemplate<Object, Object> redisTemplate;

	
	@Test
	public void contextLoads() {
		// logger.debug("dataSource=:{}",dataSource);
		// logger.debug("stringRedisTemplate=:{}",stringRedisTemplate);
		// logger.debug("redisTemplate=:"+redisTemplate);
		Boolean key = stringRedisTemplate.hasKey("key1");
		logger.debug("redis中是否有key1的值" + key);
		stringRedisTemplate.opsForValue().set("key1", "value1");
		String string = stringRedisTemplate.opsForValue().get("key1");
		System.out.println("======================================================");
		System.out.println("redis中key1的值" + string);

	}

	@Autowired
	SmsTemplate smstemplate;

	@Test
	public void testSms() {
		String uuid = UUID.randomUUID().toString().replace("-", "").substring(0,6);
		boolean flag = smstemplate.testSms("15566286945", uuid, "TP1711063");
		log.debug("验证码{},{}",uuid,flag);
	}
}
