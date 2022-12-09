package com.rnrcompany.cameltutorial.components;

import com.rnrcompany.cameltutorial.processor.InboundSalaryMessageProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.beanio.BeanIODataFormat;
import org.beanio.InvalidRecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//@Component
public class CSVFileRouteTransformer extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVFileRouteTransformer.class);

    @Autowired
    @Qualifier("beanIOEmployeeSalary")
    private BeanIODataFormat beanIODataFormat;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).process(exchange -> {
                Throwable caught = exchange.getProperty(Exchange.EXCEPTION_CAUGHT,
                    Throwable.class);

                LOGGER.error("FATAL ERROR - ", caught);
                if(caught instanceof InvalidRecordException){
                    InvalidRecordException ir = ((InvalidRecordException) caught);
                    LOGGER.error("In record number" + ir.getRecordCount());
                    LOGGER.error("In record number" + ir.getRecordContext(0).getRecordText() );
                }
                exchange.getContext().getShutdownStrategy().setLogInflightExchangesOnTimeout(false);
                exchange.getContext().getShutdownStrategy().setTimeout(5);
                exchange.getContext().stop();
            }).to("log:foo");

        from("file:src/data/input?fileName=Employee_Salaries_-_2021.csv")
            .routeId("csvFileMoveTransformerRouteId")
            .split(body().tokenize(System.lineSeparator(), 1, true))
            .unmarshal(beanIODataFormat)
            .process(new InboundSalaryMessageProcessor())
            .log(LoggingLevel.INFO, "Transformed body: ${body}")
            .convertBodyTo(String.class)
            .to("file:src/data/output?fileName=outputFile.txt&fileExist=append&appendChars=" + System.lineSeparator())
            .end(); //end the process
    }
}
