package com.liuwei.scw.user.service;

import java.util.List;

import com.liuwei.scw.common.vo.response.UserResponseVo;
import com.liuwei.scw.user.bean.TMemberAddress;
import com.liuwei.scw.user.vo.request.UserRegisterVo;

public interface UserService {

	void saveUser(UserRegisterVo vo);

	UserResponseVo doLogin(String loginacct, String userpswd);

	List<TMemberAddress> getAddress(Integer id);

}
