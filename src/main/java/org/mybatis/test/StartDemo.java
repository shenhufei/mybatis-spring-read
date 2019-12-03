package org.mybatis.test;

import org.mybatis.test.mapper.UserMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class StartDemo {

	public static void main(String[] args) {
	@SuppressWarnings("resource")
	AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
	ac.register(Appconfig.class);
	ac.refresh();
	//ConfigurableListableBeanFactory beanFactory = ac.getBeanFactory(); SqlSessionFactoryBean
	Object bean2 = ac.getBean("sqlSessionFactoryBean");
	UserMapper  userMapper  = ac.getBean(UserMapper.class);
	System.out.println(bean2);
	System.out.println(userMapper);
	
	}

}
