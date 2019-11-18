package com.liuwei.scw.user.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
@Api(tags="测试Swagger的controller")
@RestController
@Slf4j
public class HelloController {
//	Logger logger = LoggerFactory.getLogger(getClass());
	
	@GetMapping("/set")
	public String setSession(HttpSession session) {
		session.setAttribute("msg", "Hello");
		return "ok";
	}

	@GetMapping("/get")
	public String getSession(HttpSession session) {
		return (String) session.getAttribute("msg");
	}
	
	
	@ApiOperation(value="测试方法hello")
	@ApiImplicitParams(value= {
			@ApiImplicitParam(required=true,name="username"),
			@ApiImplicitParam(required=false,name="password",dataType="Integer")
	})
	@GetMapping("/hello")
	public String hello(String name,Integer password) {
		return "姓名是："+name+"密码是："+password;
	}
	
	/*@ApiOperation(value="注册方法regist")
	@PostMapping("/regist")
	public String regist(TAdmin admin) {
		log.debug("注册的TAdmin是："+admin);
		return "注册成功";
	}*/
}
