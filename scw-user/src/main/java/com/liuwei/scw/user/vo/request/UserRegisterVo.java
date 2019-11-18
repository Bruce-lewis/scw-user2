package com.liuwei.scw.user.vo.request;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRegisterVo implements Serializable{
	/**
	 * 
	 */
	private String loginacct;// phonenumber
	private String userpswd;
	private String email;
	private String code;
	private String usertype;
}
