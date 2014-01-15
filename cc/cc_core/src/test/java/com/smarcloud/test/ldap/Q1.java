package com.smarcloud.test.ldap;



import javax.naming.Name;

import org.springframework.ldap.core.DistinguishedName;

import com.smarcloud.core.annotation.ldap.Attribute;
import com.smarcloud.core.annotation.ldap.Dn;
import com.smarcloud.core.annotation.ldap.Entry;
import com.smarcloud.core.annotation.ldap.Id;
import com.smarcloud.core.ldap.entity.BaseEntityLDAP;

@Entry(objectClass={"top","smartPeople"})
public class Q1 extends BaseEntityLDAP {
    
    @Id
    private Name id = new DistinguishedName("ou=People,dc=smarcloud,dc=com");
    @Attribute(name="sn")
    private String sn;
    @Attribute(name="userPassword")
    private String userPassword;
    
    @Attribute(name="cn")
    private String cn;
    
    @Dn
    @Attribute(name="uid")
    private String uid;
    
    public String getSn() {
        return sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getUserPassword() {
        return userPassword;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public String getCn() {
        return cn;
    }
    public void setCn(String cn) {
        this.cn = cn;
    }
    public javax.naming.Name getId() {
        return id;
    }
    public void setId(javax.naming.Name id) {
        this.id = id;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    
    
}
