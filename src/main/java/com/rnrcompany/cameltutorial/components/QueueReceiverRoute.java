package com.rnrcompany.cameltutorial.components;

import static com.rnrcompany.cameltutorial.util.Constants.EMP_SAL_QUEUE;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//@Component
public class QueueReceiverRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("activemq:queue:" + EMP_SAL_QUEUE)
            .routeId("queueReceiverId")
            .log(LoggingLevel.INFO, String.format(">>>>Message received from Queue : %s<<<<", "${body}"));

    }
}
