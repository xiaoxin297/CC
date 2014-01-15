package com.smarcloud.core.ldap.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.smarcloud.core.ldap.entity.BaseEntityLDAP;

@Repository
public class BaseDaoLDAP<T extends BaseEntityLDAP> extends LDAPDaoSuport<T> {

	/**
	 * 创建新条目<br/>
	 * 
	 * @return true:创建成功 false:创建失败
	 * @param t
	 */
	public boolean bind(T t) {
		try {
			super.create(t);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 更新条目<br/>
	 * 重新此方法是可条用父类方法获取DN
	 * 
	 * @param t
	 */
	public boolean modif(T t) {
		try {
			super.update(t);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 根据DN删除条目
	 * 
	 * @param t
	 */
	public boolean unbind(T t) {
		try {
			super.delete(t);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public T findByID(T t){
		try{
			t = super.findByID(t);
		}catch(Exception e){
			e.printStackTrace();
			t = null;
		}
		return t;
	}

	@Override
	public List<T> findListByWhiteFilter(T t) {
		List<T> list = null;
		try{
			list = super.findListByWhiteFilter(t);
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return  new ArrayList<>();
	}

	@Override
	public List<T> findListByEqFilter(T t) {
		List<T> list = null;
		try{
			list = super.findListByEqFilter(t);
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	 
}
