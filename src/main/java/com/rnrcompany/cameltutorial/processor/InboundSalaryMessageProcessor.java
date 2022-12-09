package com.rnrcompany.cameltutorial.processor;

import com.rnrcompany.cameltutorial.beans.EmployeeSalary;
import com.rnrcompany.cameltutorial.beans.OutBoundSalary;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboundSalaryMessageProcessor implements Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundSalaryMessageProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        EmployeeSalary employeeSalary = exchange.getIn().getBody(EmployeeSalary.class);
        exchange.getIn().setBody(toOutBoundEmployee(employeeSalary));
    }

    private OutBoundSalary toOutBoundEmployee(EmployeeSalary empSal) {
        return new OutBoundSalary(empSal.getSalary());
    }
}
