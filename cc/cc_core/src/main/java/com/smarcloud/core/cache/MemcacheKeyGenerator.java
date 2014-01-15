package com.smarcloud.core.cache;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;

/**
 * 自定义catch key的生成规则
 * @author robin wang
 *
 */
public class MemcacheKeyGenerator implements KeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(target.getClass().getName());
		sb.append(".");
		sb.append(method.getName());
		sb.append("(");
		for (Object o : params) {
			sb.append(o.toString());
			sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}

}
