package com.smarcloud.web.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;

public class Test {

    public static void main(String[] args) {
	// 请求参数name=王继永&age=28
	A1 a1 = new A1();
	
	// Contorller 使用
	Base base = a1;
	String name  = (String)a1.get("name");
	System.out.println(name);
	
	// Base --> PO
	//A11 a11 = new A11();
	//BeanUtils.copyProperties(base, a11);
	//System.out.println(a11.getName());

    }
}
