package com.smarcloud.common.exception;

import org.springframework.stereotype.Component;

@Component
public class SmarcloudException extends Exception {
    
    private static final long serialVersionUID = -3873989445332724945L;
    
    private String message = "";
    private int state = 0;
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public int getState() {
        return state;
    }
    public void setState(int state) {
        this.state = state;
    }
    
}
