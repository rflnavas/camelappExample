package com.rnrcompany.cameltutorial.components;

import com.rnrcompany.cameltutorial.beans.EmployeeSalary;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.beanio.BeanIODataFormat;
import org.apache.camel.spi.ShutdownStrategy;
import org.beanio.InvalidRecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//Deactivated as this component takes the same input file

/*
    org.apache.camel.FailedToStartRouteException:
 Failed to start route csvFileMoveTransformerRouteId because of Multiple consumers for the same
 endpoint is not allowed: file://src/data/input?fileName=Employee_Salaries_-_2021.csv
*/

//@Component
public class CSVFileRoute extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVFileRoute.class);

    @Autowired
    @Qualifier("beanIOEmployeeSalary")
    private BeanIODataFormat beanIODataFormat;

    @Override
    public void configure() throws Exception {
        /*
         * Since the process may fail it is essential to provide a RouteBuilder for handling exceptions
         * One of the possible causes is that a given row can be flaw, hence the row processing will fail.
         */
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
        /*
         * As soon as a file whose name is inputFile.csv is moved to src/data/input
         * the process will be invoked, where we have the chance to log its content, and then copied to new file,
         * outputFile.txt. Contents of the latter file will be replaced if it already exists
         */
        from("file:src/data/input?fileName=Employee_Salaries_-_2021.csv")
            .routeId("csvFileMoveRouteId")//Make sure you are using unique IDs that do not exist for other camel
            //We are using an EIP splitter. We split the CSV file by line separator
            //With skipFirst=true the first fromthe csv file will not logged in console.
            .split(body().tokenize(System.lineSeparator(), 1, true))
            .unmarshal(beanIODataFormat)
            .process(exchange -> {
                EmployeeSalary employeeSalary = exchange.getIn().getBody(EmployeeSalary.class);
                employeeSalary.setDepartment("_" + employeeSalary.getDepartment());
                //We can also use convertBodyTo instead
                //exchange.getIn().setBody(employeeSalary.toString());
            })
            .convertBodyTo(String.class)
            //.log("CamelBatchSize (property) = ${property.CamelBatchSize}")
            //.log("CamelBatchSize (property) = ${property.CamelBatchComplete}")
            /*The result will only contain the last token, that is the file will not be appended, unless you set the
             fileExist parameter set as append. Additionally, we must inform that every token must appended with the
             line separator as this symbol no longer takes part from the body. That's the reason why we use the
             appendChars parameter*/
            .to("file:src/data/output?fileName=outputFile.txt&fileExist=append&appendChars=" + System.lineSeparator())
            .end(); //end the process
    }
}
