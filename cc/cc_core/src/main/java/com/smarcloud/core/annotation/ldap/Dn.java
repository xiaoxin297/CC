package com.smarcloud.core.annotation.ldap;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.naming.Name;

/**
 * LDAP中条目的dn
 * @author robin wang
 * @see javax.naming.Name
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Dn {
    
}
