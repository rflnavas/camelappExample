package com.rnrcompany.cameltutorial.processor;

import com.rnrcompany.cameltutorial.beans.EmployeeSalary;
import com.rnrcompany.cameltutorial.beans.OutBoundEmployee;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class InboundEmployeeProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        EmployeeSalary employeeSalary = exchange.getIn().getBody(EmployeeSalary.class);
        exchange.getIn().setBody(getOutboundEmployee(employeeSalary));
        exchange.getIn().setHeader("processedId", employeeSalary.getId());
    }

    private OutBoundEmployee getOutboundEmployee(EmployeeSalary employeeSalary) {
        return new OutBoundEmployee(
            String.format("%s - %s", employeeSalary.getDepartment(), employeeSalary.getDepartmentName()),
                employeeSalary.getSalary());
    }
}
