package com.smarcloud.test.ldap;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DistinguishedName;

import com.smarcloud.core.ldap.entity.BaseEntityLDAP;
import com.smarcloud.test.TestBase;


public class Test extends TestBase {

    @Autowired
    Q1Dao q1Dao;
    @Autowired
    Q11Dao q11Dao;
    @org.junit.Test
    public void main() {
    	Q1 ldap = new Q1();
    	ldap.setSn("sn");
    	List<Q1> q2 = q1Dao.findListByWhiteFilter(ldap);
    	for(Q1 q :q2){
    		q1Dao.unbind(q);
    	} 
//    	ldap = q1Dao.findByID(ldap);
//    	if(ldap == null){
//    		ldap = new Q1();
//    		ldap.setUid("wjywj789");
//    		ldap.setCn("this is cn");
//    		ldap.setSn("this is sn");
//    		ldap.setUserPassword("123456");
//    		q1Dao.bind(ldap);
//    	}
//    	ldap = q1Dao.findByID(ldap);
//    	System.out.println(ldap.getCn());
    }
}
