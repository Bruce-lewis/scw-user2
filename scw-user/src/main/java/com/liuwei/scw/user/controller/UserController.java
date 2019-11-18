package com.liuwei.scw.user.controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.liuwei.scw.common.bean.ResponseVo;
import com.liuwei.scw.common.vo.response.UserResponseVo;
import com.liuwei.scw.user.bean.TMemberAddress;
import com.liuwei.scw.user.service.UserService;
import com.liuwei.scw.user.utils.ScwUserAppUtil;
import com.liuwei.scw.user.utils.SmsTemplate;
import com.liuwei.scw.user.vo.request.UserRegisterVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "处理用户登录、注册、短信验证等需求")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	SmsTemplate smsTemplate;
	@Autowired
	UserService userService;

	// 查询用户的地址集合
	@ApiOperation(value = "查询用户的地址集合")
	@GetMapping("/getAddress")
	public ResponseVo<List<TMemberAddress>> getAddress(@RequestParam("accessToken") String accessToken) {
		// 根据token去redis中获取 用户信息
		String userInfoStr = stringRedisTemplate.opsForValue().get(accessToken);
		UserResponseVo vo = JSON.parseObject(userInfoStr, UserResponseVo.class);
		if (vo == null) {
			return ResponseVo.fail("登录超时");
		}
		Integer id = vo.getId();
		log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		log.info("id"+id);
		List<TMemberAddress> list = userService.getAddress(id);
		log.info("list:"+list);
		return ResponseVo.ok(list);
	}

	@ApiOperation(value = "登录方法")
	@PostMapping("/doLogin")
	public ResponseVo<UserResponseVo> doLogin(@RequestParam("loginacct") String loginacct,
			@RequestParam("userpswd") String userpswd) {
		UserResponseVo vo;
		try {
			vo = userService.doLogin(loginacct, userpswd);
			return ResponseVo.ok(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	@ApiOperation(value = "注册方法")
	@PostMapping("/doRegist")
	public ResponseVo<Object> doRegist(UserRegisterVo vo) {
		// 1、接收参数
		// 2、调用业务层处理保存数据的业务
		try {
			userService.saveUser(vo);
			// 3、给调用者响应
			return ResponseVo.ok("注册成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseVo.fail(e.getMessage());
		}
	}

	// 处理发送短信验证码请求
	@ApiOperation(value = "注册时获取验证码的方法")
	@ApiImplicitParams(value = { @ApiImplicitParam(name = "phoneNum", value = "手机号码") })
	@GetMapping(value = "/sendSms")
	public String sendSms(String phoneNum) {
		int count = 0;
		boolean flag = ScwUserAppUtil.isMobile(phoneNum);
		if (!flag) {
			return "手机号码格式错误";
		}
		String phoneCountKey = "phone:code:" + phoneNum + ":count";
		flag = stringRedisTemplate.hasKey(phoneCountKey);
		if (flag) {
			String str = stringRedisTemplate.opsForValue().get(phoneCountKey);
			count = Integer.parseInt(str);
			if (count >= 3) {
				return "今日次数已经用完";
			}
		}
		// 3、判断手机号码是否存在未使用的验证码
		// 拼接该手机号码存储验证码的key
		String phoneCodeKey = "phone:code:" + phoneNum + ":code";
		String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
		flag = smsTemplate.testSms(phoneNum, code, "TP1711063");
		if (!flag) {
			return "短信发送失败，请稍后再试";
		}
		stringRedisTemplate.opsForValue().set(phoneCodeKey, code, 15, TimeUnit.MINUTES);
		if (count == 0) {
			// 第一次记录次数
			stringRedisTemplate.opsForValue().set(phoneCountKey, "1", 24, TimeUnit.HOURS);
		} else {
			count++;
			stringRedisTemplate.opsForValue().increment(phoneCountKey);
		}
		// 8、给出成功响应
		return "发送验证码成功";
	}
}
