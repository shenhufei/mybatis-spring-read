package org.mybatis.test;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.test.mapper.UserMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
@MapperScan("org.mybatis.test.mapper")
public class StartDemo {

	public static void main(String[] args) {
	@SuppressWarnings("resource")
	AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
	ac.register(Appconfig.class);
	ac.refresh();
	//ConfigurableListableBeanFactory beanFactory = ac.getBeanFactory();
	Object bean2 = ac.getBean(UserMapper.class);
	
	}

}
