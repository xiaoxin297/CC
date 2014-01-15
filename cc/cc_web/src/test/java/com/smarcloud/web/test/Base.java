package com.smarcloud.web.test;

import java.lang.reflect.InvocationTargetException;

public class Base {
    private Base base;
    
    public Base(){
	base = this;
    }

    public Object get(String key){
	Class clazz =  base.getClass();
	try {
	    try {
		return clazz.getMethod("get"+key.substring(0,1).toUpperCase() + key.substring(1), null).invoke(this, null);
	    } catch (IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException e) {
		e.printStackTrace();
	    }
	} catch (NoSuchMethodException | SecurityException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
