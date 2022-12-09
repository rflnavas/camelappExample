package com.rnrcompany.cameltutorial.components;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//@Component
public class SimpleTimer extends RouteBuilder {

    public static final String SIMPLE_TIMER_ID="simpleTimerId";
    @Override
    public void configure() throws Exception {
        from("timer:simpletimer?period=2000")
                .routeId(SIMPLE_TIMER_ID)
                .setBody(constant("Hola mundo!"))
                .log(LoggingLevel.INFO, "Captured this message : ${body}");
    }
}
