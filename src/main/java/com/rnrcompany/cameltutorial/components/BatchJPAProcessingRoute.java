package com.rnrcompany.cameltutorial.components;

import com.rnrcompany.cameltutorial.beans.EmployeeSalary;
import com.rnrcompany.cameltutorial.processor.InboundEmployeeProcessor;
import com.rnrcompany.cameltutorial.processor.InboundSalaryMessageProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//@Component
public class BatchJPAProcessingRoute extends RouteBuilder {

    private static final String FIND_ALL_NAMED_QUERY = "findAll";
    @Override
    public void configure() throws Exception {
        /*
         *   Without splitting the log output is shown something like:
         *   'Fetched rows: Row1, Row2, ..., RowN'
         *   By doing it so the output is more readable
         *      'Fetched rows: Row1'
         *          ...
         *      'Fetched rows: RowN'
         */
        from("timer:readDB?period=10000")
            .routeId("readDBId")
            .to(String.format("jpa:%s?namedQuery=%s", EmployeeSalary.class.getName(), FIND_ALL_NAMED_QUERY))
            .split(body())
                .process(new InboundEmployeeProcessor())
                .log(LoggingLevel.INFO, "Fetched rows: ${body}")
                .convertBodyTo(String.class)
                .to("file:src/data/output?fileName=outputFile.txt&fileExist=append&appendChars=\\n")
                /*   Once the data is stored in txt file we go on with the data deletion in DB
                 *   BUT with .to method, the process will fail since we have an static url that does not change the
                 *   header.processedId value dynamically while processing disparate row identifiers.
                 *  .toD will calculate the id right before the next jpa executio.
                 */
                .toD(String.format("jpa:%s?nativeQuery=%s%s", EmployeeSalary.class.getName(),
                    "DELETE FROM EMPLOYEE_SALARY WHERE id = ${header.processedId}",
                    "&useExecuteUpdate=true"))
            .end();
    }
}
