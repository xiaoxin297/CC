package com.smarcloud.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/*.xml"})
public class TestBase {

    @Test
    public void test(){
	System.err.println("______________________________________________________________");
	System.err.println("______________________________________________________________");
	System.err.println("________________________-—-测试结束---___________________________");
	System.err.println("______________________________________________________________");
	System.err.println("______________________________________________________________");
    }
}
