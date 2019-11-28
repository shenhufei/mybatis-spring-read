package org.mybatis.test;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@ComponentScan("org.mybatis.test")
// 已经加了扫描 mapper层接口的 路径
@MapperScan("org.mybatis.test.mapper")
@Configuration
public class Appconfig {
	
	 
		@Bean("name")
		public SqlSessionFactoryBean   sqlSessionFactoryBean(DataSource dataSource){
			SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
			bean.setDataSource(dataSource);
			return bean;
		}
		
		@Bean
		public DataSource dataSource(){
			DriverManagerDataSource  drive = new  DriverManagerDataSource();
			drive.setDriverClassName("com.mysql.jdbc.Driver");
			drive.setUrl("jdbc:mysql://cdb-ewlmquzk.bj.tencentcdb.com:10191/data_shf");
			drive.setUsername("root");
			drive.setPassword("shenhufei_");
			return drive;
		}
	
	
	
	
	
	
}
