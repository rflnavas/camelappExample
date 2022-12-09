package com.rnrcompany.cameltutorial.aggregators;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import java.math.BigDecimal;

public class SalaryMeanAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            newExchange.getIn().setHeader("total", 1);
            return newExchange;
        }

        BigDecimal previousQuantity = getBigDecimalFromBody(oldExchange);
        BigDecimal nextQuantity = getBigDecimalFromBody(newExchange);

        BigDecimal result = previousQuantity.add(nextQuantity);
        newExchange.getIn().setBody(result.toPlainString());
        int total = oldExchange.getIn().getHeader("total", Integer.class);
        newExchange.getIn().setHeader("total", ++total);
        return newExchange;
    }

    @Override
    public void onCompletion(Exchange exchange) {
        AggregationStrategy.super.onCompletion(exchange);
    }

    private BigDecimal getBigDecimalFromBody(Exchange exchange){
        String body = exchange.getIn().getBody(String.class);
        if(body == null) {
            return BigDecimal.ZERO;
        }
        try{
            return new BigDecimal(exchange.getIn().getBody(String.class));
        } catch(Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
}
