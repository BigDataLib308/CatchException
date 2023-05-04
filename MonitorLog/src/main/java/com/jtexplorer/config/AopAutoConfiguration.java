package com.jtexplorer.config;

import net.bytebuddy.asm.Advice;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.aop",name = "auto",havingValue = "true",matchIfMissing = true)
public class AopAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Advice.class)
    static class AspectJAutoProxyConfiguration{
        @Configuration(proxyBeanMethods = false)
        @EnableAspectJAutoProxy(proxyTargetClass = false)
        @ConditionalOnProperty(prefix = "spring.aop",name = "proxy-target-class",havingValue = "false",matchIfMissing = false)
        static class  JdkDynamicAutoProxyConfiguration{

        }
        @Configuration(proxyBeanMethods = false)
        @EnableAspectJAutoProxy(proxyTargetClass = false)
        @ConditionalOnProperty(prefix = "spring.aop",name = "proxy-target-class",havingValue = "true",matchIfMissing = true)
        static class CgliAutoProxyConfiguration{

        }

    }
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass("org.aspect.weaver.Advice")
    @ConditionalOnProperty(prefix = "spring.aop",name = "proxy-target-class",havingValue = "true",matchIfMissing = true)
    static class ClassproxyingConfiguration{
        ClassproxyingConfiguration(BeanFactory beanFactory){
            if (beanFactory instanceof BeanDefinitionRegistry){
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
                AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
            }
        }
    }

}
