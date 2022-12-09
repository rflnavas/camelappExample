package com.rnrcompany.cameltutorial.components;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Collections;
import java.util.List;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
public class CSVFileRouteTest {

    private static final String EMPLOYEE_SALARY_CSV_HEADER = "department,departmentName,division,gender,salary,"
        + "overtimePay,longetivityPay,grade\n";

    @Autowired
    CamelContext camelContext;

    @EndpointInject("mock:result")
    protected MockEndpoint mockEndpoint;

    @Autowired ProducerTemplate producerTemplate;

    @Test
    public void testFileMove() throws Exception{
        //init mock
        List<String> expectedBody = Collections.singletonList("[EmployeeSalary(id=null, department=_ABS, "
        + "departmentName=Alcohol Beverage Services, division=Beer Loading, gender=M, salary=87969, overtimePay=32953.25, "
        + "longetivityPay=, grade=20)]");
        mockEndpoint.expectedBodiesReceived(expectedBody);
        mockEndpoint.expectedMinimumMessageCount(1);
        //tweak route definition
        AdviceWith.adviceWith(camelContext, "csvFileMoveRouteId",
            routeBuilder -> {
                routeBuilder.weaveByToUri("file:*").replace().to(mockEndpoint);
            });

        //ctx and validate
        camelContext.start();
        mockEndpoint.assertIsSatisfied(10_000);

    }

    @Test
    public void testFileMoveMockingFromEndPoint() throws Exception{
        //init mock
        final String bodyToSend = EMPLOYEE_SALARY_CSV_HEADER
                + "ABS,Alcohol Beverage Services,Beer Loading,M,87969,32953.25,,20";
        final List<String> expectedBody = Collections.singletonList("[EmployeeSalary(id=null, department=_ABS, "
        + "departmentName=Alcohol Beverage Services, division=Beer Loading, gender=M, salary=87969, overtimePay=32953.25, "
        + "longetivityPay=, grade=20)]");

        mockEndpoint.expectedBodiesReceived(expectedBody);
        mockEndpoint.expectedMinimumMessageCount(1);
        //tweak route definition
        AdviceWith.adviceWith(camelContext, "csvFileMoveRouteId",
            routeBuilder -> {
                routeBuilder.replaceFromWith("direct:mockStart");
                routeBuilder.weaveByToUri("file:*").replace().to(mockEndpoint);
            });

        //ctx and validate
        camelContext.start();

        producerTemplate.sendBody("direct:mockStart", bodyToSend);
        mockEndpoint.assertIsSatisfied();

    }
}
