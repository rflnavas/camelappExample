package com.rnrcompany.cameltutorial.components;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
public class LegacyFileRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyFileRoute.class);

    @Override public void configure() throws Exception {
        /*
         * As soon as a file whose name is inputFile.txt is moved to src/data/input
         * the process will be invoked, where we have the chance to log its content, and then a new output.txt file
         * is generated
         */
        from("file:src/data/input?fileName=inputFile.txt")
            .routeId("legacyFileMoveRouteId")
            .process(exchange -> {
                String fileData = exchange.getIn().getBody(String.class);
                LOGGER.info("This is the file data" + fileData);
            })
            .to("file:src/data/output?fileName=outputFile.txt");
    }
}
