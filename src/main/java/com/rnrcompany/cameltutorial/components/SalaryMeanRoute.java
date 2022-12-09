package com.rnrcompany.cameltutorial.components;

import com.rnrcompany.cameltutorial.aggregators.SalaryMeanAggregator;
import com.rnrcompany.cameltutorial.beans.EmployeeSalary;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.beanio.BeanIODataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class SalaryMeanRoute extends RouteBuilder {

    @Autowired
    @Qualifier("beanIOEmployeeSalary")
    private BeanIODataFormat beanIODataFormat;

    @Override
    public void configure() throws Exception {

        from("file:src/data/input?fileName=Employee_Salaries_-_2021.csv")
            .routeId("salaryMeanId")
            .split(body().tokenize(System.lineSeparator(), 1, true))
            .unmarshal(beanIODataFormat)
            .process(exchange -> {
                EmployeeSalary es = exchange.getIn().getBody(EmployeeSalary.class);
                exchange.getIn().setBody(es.getSalary());
               }
            )
            //We need to set a header whose key name is equals to aggregation header.
            //In other words, we are creating a correlation key. Other wise we will face this exception.
            //org.apache.camel.CamelExchangeException: Invalid correlation key. Exchange[]
            .setHeader("totalSalary", simple("{body}"))
            .aggregate(header("totalSalary"), new SalaryMeanAggregator())
            .completionTimeout(2_000)
            .choice()
            .when(exchangeProperty("CamelSplitComplete").isEqualTo("true"))
                .log(LoggingLevel.INFO, "Splitted ${header.CamelSplitSize} records. Total salary summed: ${body}")
                .process(exchange -> {
                   //Cannot rely on this approach
                   // exchange.getIn().setHeader("total", header("CamelSplitSize"));
                    BigDecimal totalSummedSalary = exchange.getIn().getBody(BigDecimal.class);
                    BigDecimal divisor = new BigDecimal(exchange.getIn().getHeader("total").toString());
                    BigDecimal mean = totalSummedSalary.divide(divisor, RoundingMode.UP);
                    exchange.getIn().setBody(mean);
                 })
             .otherwise()
                .log(LoggingLevel.INFO, "Not completed yet")
            .log(LoggingLevel.INFO, "Mean salary is : {body}")
            .to("file:src/data/output?fileName=salaryMean.txt&appendChars=" + System.lineSeparator());
    }
}
