package com.rnrcompany.cameltutorial.configuration;

import org.apache.camel.dataformat.beanio.BeanIODataFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

    @Bean
    @Qualifier("beanIOEmployeeSalary")
    public BeanIODataFormat beanIoEmployeeSalary(){
        return new BeanIODataFormat("InboundMessageBeanIOMessaging.xml","inputMessageStream");
    }
}
