package com.rnrcompany.cameltutorial.components;

import com.fasterxml.jackson.core.JsonParseException;
import com.rnrcompany.cameltutorial.beans.EmployeeSalary;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

//@Component
public class EmployeeRestRoute extends RouteBuilder {

    @Override public void configure() throws Exception {

        onException(JsonParseException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().constant("Invalid json data");

        restConfiguration()
            .component("jetty")
            .host("0.0.0.0")
            .port(9080)
            .bindingMode(RestBindingMode.json)
            .enableCORS(true);

        rest("rnrcompany")
            .produces("application/json")
            .post("employee")
            .type(EmployeeSalary.class)
            .route()
            .routeId("restEmployeeId")
            .log(LoggingLevel.INFO, "${body}")
            .to("jpa:" + EmployeeSalary.class.getName());
    }
}
