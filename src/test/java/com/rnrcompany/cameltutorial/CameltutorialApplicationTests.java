package com.rnrcompany.cameltutorial;

import com.rnrcompany.cameltutorial.components.SimpleTimer;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
class CameltutorialApplicationTests {
	@Autowired
	CamelContext context;

	@EndpointInject("mock:result")
	protected MockEndpoint mockEndpoint;

	@Test
	void testSimpleTimer() throws Exception {
		final String expectedBody = "Hola mundo!";
		mockEndpoint.expectedBodiesReceived(expectedBody);
		mockEndpoint.expectedMinimumMessageCount(1);
		AdviceWith.adviceWith(context, SimpleTimer.SIMPLE_TIMER_ID,
				routeBuilder -> routeBuilder.weaveAddLast().to(mockEndpoint));
		context.start();
		mockEndpoint.assertIsSatisfied();
	}

}
