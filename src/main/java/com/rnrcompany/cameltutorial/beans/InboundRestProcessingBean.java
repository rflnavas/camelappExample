package com.rnrcompany.cameltutorial.beans;

import org.apache.camel.Exchange;

public class InboundRestProcessingBean {

    /*
     * As this class takes part from a camel processing, if you define a step as .bean(new InboundRestProcessingBean())
     *  it can only contain 1 method. It can be named as you want. Otherwise you will face this error:
     *  org.apache.camel.component.bean.AmbiguousMethodCallException: Ambiguous method invocations
     */
    public void validate(Exchange exchange){
        EmployeeSalary employeeSalary = exchange.getIn().getBody(EmployeeSalary.class);
        exchange.getIn().setHeader("dpt", employeeSalary.getDepartment());
        exchange.getIn().setHeader("salary", employeeSalary.getSalary());
    }

    /*
     * Unless you explicitly point the method you want to invoke on the bean processing
     * .bean(new InboundRestProcessingBean(), "newValidation")
     */
     public void newValidation(Exchange exchange){
        EmployeeSalary employeeSalary = exchange.getIn().getBody(EmployeeSalary.class);
        exchange.getIn().setHeader("ovtpay", employeeSalary.getOvertimePay());
        exchange.getIn().setHeader("salary", employeeSalary.getSalary());
        exchange.getIn().setHeader("grade", employeeSalary.getGrade());
        exchange.getIn().setHeader("gender", employeeSalary.getGender());
     }

}
