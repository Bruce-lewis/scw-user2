package com.liuwei.scw.user.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.google.gson.Gson;
import com.liuwei.scw.common.consts.UserAppConsts;
import com.liuwei.scw.common.vo.response.UserResponseVo;
import com.liuwei.scw.user.bean.TMember;
import com.liuwei.scw.user.bean.TMemberAddress;
import com.liuwei.scw.user.bean.TMemberAddressExample;
import com.liuwei.scw.user.bean.TMemberExample;
import com.liuwei.scw.user.exception.UserException;
import com.liuwei.scw.user.mapper.TMemberAddressMapper;
import com.liuwei.scw.user.mapper.TMemberMapper;
import com.liuwei.scw.user.service.UserService;
import com.liuwei.scw.user.vo.request.UserRegisterVo;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	TMemberMapper memberMapper;
	@Autowired
	BCryptPasswordEncoder encoder;
	@Autowired
	TMemberAddressMapper memberAddressMapper;

	@Override
	public void saveUser(UserRegisterVo vo) {
		// 1、判断验证码
		String phoneCodeKey = UserAppConsts.PHONE_CODE_PREFIX+vo.getLoginacct()+UserAppConsts.PHONE_CODE_SUFFIX;
		Boolean flag = stringRedisTemplate.hasKey(phoneCodeKey);
		if(!flag) {
			throw new UserException("验证码错误【不存在或者已经过期】");
		}
		String redisPhoneCode = stringRedisTemplate.opsForValue().get(phoneCodeKey);
		if(StringUtils.isEmpty(vo.getCode())||!vo.getCode().equals(redisPhoneCode)) {
			throw new UserException("验证码输入不正确");
		}
		// 2、将UserRegisterVo转为TMember对象，并初始化默认值
		TMember member  = new TMember();
		BeanUtils.copyProperties(vo, member);
		member.setUsername(member.getLoginacct());
		member.setAuthstatus("0");
		// 3、调用mapper将数据存到数据库中
		TMemberExample example = new TMemberExample();
		// 3.1 验证账号和邮箱的唯一性
		example.createCriteria().andLoginacctEqualTo(member.getLoginacct());
		long l = memberMapper.countByExample(example);
		if(l>0) {
			//用户名被占用
			throw new UserException("用户名已经被占用");
		}
		example.clear();
		example.createCriteria().andEmailEqualTo(member.getEmail());
		l = memberMapper.countByExample(example);
		if(l>0) {
			throw new UserException("邮箱已被占用");
		}
		// 3.2、保存
		// 密码加密
		member.setUserpswd(encoder.encode(member.getUserpswd()));
		memberMapper.insertSelective(member);
		// 4、给出返回值
	}

	@Override
	public UserResponseVo doLogin(String loginacct, String userpswd) {
		TMemberExample example = new TMemberExample();
		example.createCriteria().andLoginacctEqualTo(loginacct);
		List<TMember> list = memberMapper.selectByExample(example);
		if(CollectionUtils.isEmpty(list)||list.size()>1) {
			throw new UserException("账号不存在");
		}
		TMember member = list.get(0);
		boolean flag = encoder.matches(userpswd, member.getUserpswd());
		if(!flag) {
			throw new UserException("密码错误");
		}
		UserResponseVo vo = new UserResponseVo();
		BeanUtils.copyProperties(member, vo);
		String token = UUID.randomUUID().toString().replace("-", "");
		token = UserAppConsts.USER_LOGIN_TOKEN_PREFIX+token;	
		vo.setAccessToken(token);
		
		//将登录成功的信息存到redis中
		//可以将对象转为json字符串，存到redis中
		Gson gson = new Gson();
		String voJsonStr = gson.toJson(vo);
		stringRedisTemplate.opsForValue().set(token, voJsonStr,7,TimeUnit.DAYS);
		return vo;
	}

	@Override
	public List<TMemberAddress> getAddress(Integer id) {
		TMemberAddressExample example = new TMemberAddressExample();
		example.createCriteria().andMemberidEqualTo(id);
		return memberAddressMapper.selectByExample(example );
	}

}
