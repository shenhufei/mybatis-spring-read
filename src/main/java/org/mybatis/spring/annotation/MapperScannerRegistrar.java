/**
 * Copyright 2010-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mybatis.spring.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;

/**
 * A {@link ImportBeanDefinitionRegistrar} to allow annotation configuration of MyBatis mapper scanning. Using
 * an @Enable annotation allows beans to be registered via @Component configuration, whereas implementing
 * {@code BeanDefinitionRegistryPostProcessor} will work for XML configuration.
 *
 * @author Michael Lanyon
 * @author Eduardo Macarron
 * @author Putthiphong Boonphong
 *
 * @see MapperFactoryBean
 * @see ClassPathMapperScanner
 * @since 1.2.0
 */
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
  private static final Logger logger = LoggerFactory.getLogger(MapperScannerRegistrar.class);
	    
  /**
   * {@inheritDoc}
   * 
   * @deprecated Since 2.0.2, this method not used never.
   */
  @Override
  @Deprecated
  public void setResourceLoader(ResourceLoader resourceLoader) {
    // NOP
  }


/**
 * spring的后置处理器，调用这个方法，ImportBeanDefinitionRegistrar 接口的实现类；
 * 来把mybatis的框架的对象生命周期进行管理
 * 把mybatis的对象组装成 RootBeanDefinition 对象，存储在BeanDefinitionMap集合中
 * @date 2019年11月29日  
 * @version 1.0  
 * @author shenhufei
 */
/* invokeBeanFactoryPostProcessors(beanFactory);spring的后置处理器会调用这个方法，把mybatis的对象组装成RootBeanDefinition
 * spring的后置处理器作用：
 *  在spring 的环境中去执行已经被注册的后置处理器
	后置处理器分为：1.spring项目中自己的处理器；2.程序自定义的后置处器
	 后置处理器的作用是：1.将spring的扫描类扫描之后扫描指定包下的加了注解的类，将所有符合spring规范的类都扫描出来）将类的各种属性都存储在RootBeanDefinition对象中，处理处理各种@Import//Resources ，@Import @Mapper 注解;2.1执行源码中实现这个类的
	BeanFactoryPostProcessor接口的类，2.2执行自定义的实现
	BeanFactoryPostProcessor接口的类，也就是认为去干涉spring的初始化过程//也就是可以修改bean对象的各种属性
			
 * 
 *
 */
@Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
	/*AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
	DataSource dataSource = ac.getBean(DataSource.class);
	SqlSessionFactoryBean sqlSessionFactoryBean = ac.getBean(SqlSessionFactoryBean.class);
	logger.error("DataSource:"+JSONArray.toJSONString(dataSource));
	logger.error("SqlSessionFactoryBean:"+JSONArray.toJSONString(sqlSessionFactoryBean));*/
	//以上流程，是有问题的，根本就获取不到bean对象，因为spring还没有初始化完成；
	//拿到各种方式将对象给spring管理的配置项，
	
	AnnotationAttributes mapperScanAttrs = AnnotationAttributes
        .fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));
	logger.error("mapperScanAttrs对象数据是："+JSONArray.toJSONString(mapperScanAttrs));
	logger.error("importingClassMetadata对象数据是(Appconfig这个配置类的基础信息)："+JSONArray.toJSONString(importingClassMetadata));
	//registry 对象中包含了当前电脑，开发环境的所有基本信息；需要初始化的类：（mybatis自己的类，以及整合之后产生的中间类）
	logger.error("registry对象数据是："+JSONArray.toJSONString(registry));
    if (mapperScanAttrs != null) {
    //再根据具体的配置项，去进行存储在BeanDefinitionMap集合中的操作
      registerBeanDefinitions(mapperScanAttrs, registry, generateBaseBeanName(importingClassMetadata, 0));
    }
  }

  void registerBeanDefinitions(AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry, String beanName) {
	//建造者模式获取  BeanDefinitionBuilder 用于之后构建每个 mapper接口的 对应RootBeanDefinition 对象
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
    builder.addPropertyValue("processPropertyPlaceHolders", true);
    //拿到了AnnotationAttributes 中的数据进行相关的配置操作；
    Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
    if (!Annotation.class.equals(annotationClass)) {
      builder.addPropertyValue("annotationClass", annotationClass);
    }

    Class<?> markerInterface = annoAttrs.getClass("markerInterface");
    if (!Class.class.equals(markerInterface)) {
      builder.addPropertyValue("markerInterface", markerInterface);
    }

    Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
    if (!BeanNameGenerator.class.equals(generatorClass)) {
      builder.addPropertyValue("nameGenerator", BeanUtils.instantiateClass(generatorClass));
    }

    Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
    if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
      builder.addPropertyValue("mapperFactoryBeanClass", mapperFactoryBeanClass);
    }

    String sqlSessionTemplateRef = annoAttrs.getString("sqlSessionTemplateRef");
    if (StringUtils.hasText(sqlSessionTemplateRef)) {
      builder.addPropertyValue("sqlSessionTemplateBeanName", annoAttrs.getString("sqlSessionTemplateRef"));
    }

    String sqlSessionFactoryRef = annoAttrs.getString("sqlSessionFactoryRef");
    if (StringUtils.hasText(sqlSessionFactoryRef)) {
      builder.addPropertyValue("sqlSessionFactoryBeanName", annoAttrs.getString("sqlSessionFactoryRef"));
    }
    
    //以下三个配置项的获取，说明了，mybatis-spring的时候，注册bean对象有三种方式； value；basePackages；basePackageClasses
    //basePackages 集合就是把所有的需要给spring管理的对象 都放在一个集合中；
    List<String> basePackages = new ArrayList<>();
    basePackages.addAll(
        Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));

    basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText)
        .collect(Collectors.toList()));

    basePackages.addAll(Arrays.stream(annoAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName)
        .collect(Collectors.toList()));
    
    //大概就是获取哪些是懒加载的类
    String lazyInitialization = annoAttrs.getString("lazyInitialization");
    if (StringUtils.hasText(lazyInitialization)) {
      builder.addPropertyValue("lazyInitialization", lazyInitialization);
    }

    builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));

    registry.registerBeanDefinition(beanName, builder.getBeanDefinition());

  }

  private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
	  logger.error("AnnotationMetadata对象数据是："+JSONArray.toJSONString(importingClassMetadata));
	  return importingClassMetadata.getClassName() + "#" + MapperScannerRegistrar.class.getSimpleName() + "#" + index;
  }

  /**
   * A {@link MapperScannerRegistrar} for {@link MapperScans}.
   * 
   * @since 2.0.0
   */
  static class RepeatingRegistrar extends MapperScannerRegistrar {
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
      AnnotationAttributes mapperScansAttrs = AnnotationAttributes
          .fromMap(importingClassMetadata.getAnnotationAttributes(MapperScans.class.getName()));
      if (mapperScansAttrs != null) {
        AnnotationAttributes[] annotations = mapperScansAttrs.getAnnotationArray("value");
        for (int i = 0; i < annotations.length; i++) {
          registerBeanDefinitions(annotations[i], registry, generateBaseBeanName(importingClassMetadata, i));
        }
      }
    }
  }

}
