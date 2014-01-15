package com.smarcloud.core.cache;


import java.io.Serializable;
import java.util.concurrent.TimeoutException;



import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

public class MemcacheHanlder implements Cache,InitializingBean {
	private static Logger logger = Logger.getLogger(MemcacheHanlder.class);
	private String name;	
	private MemcachedClient memcachedClient;
	private static final int MAX_EXPIRED_DURATION = 60 * 60 * 24 * 30;
	private int expiredDuration = MAX_EXPIRED_DURATION;
	private static final Object NULL_HOLDER = new NullHolder();
	private boolean allowNullValues = true;

	private static class NullHolder implements Serializable {
		private static final long serialVersionUID = -99681708140860560L;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(memcachedClient==null){
			logger.info("memcached client does not exist");
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getNativeCache() {
		return memcachedClient;
	}

	@Override
	public ValueWrapper get(Object key) {
		Object value = null;
		try {
			value = memcachedClient.get(key.toString());
			logger.info("cache : get cache by cache key : " + key);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			e.printStackTrace();
		}
		return (value != null ? new SimpleValueWrapper(fromStoreValue(value)) : null);
	}

	@Override
	public void put(Object key, Object value) {
		try {
			boolean isOk = memcachedClient.set(key.toString(), expiredDuration, toStoreValue(value));
			if(isOk){
				logger.info("cache : set success! key : " + key);
			}else{
				logger.info("cache : set faild! key : " + key);
			}
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void evict(Object key) {
		try {
			boolean isOk = memcachedClient.delete(key.toString());
			if(isOk){
				logger.info("cache: delete success! key : " + key);
			}else{
				logger.info("cache: delete faild！key : " + key);
			}
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void clear() {
		try {
			memcachedClient.flushAll();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}

	protected Object fromStoreValue(Object storeValue) {
		if (this.allowNullValues && storeValue instanceof NullHolder) {
			return null;
		}
		return storeValue;
	}

	protected Object toStoreValue(Object userValue) {
		if (this.allowNullValues && userValue == null) {
			return NULL_HOLDER;
		}
		return userValue;
	}

	public int getExpiredDuration() {
		return expiredDuration;
	}

	public void setExpiredDuration(int expiredDuration) {
		this.expiredDuration = expiredDuration;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	
}
