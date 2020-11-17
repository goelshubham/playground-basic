package com.main;
import java.io.IOException;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.util.StopWatch;

/**
 * Interceptor to log average response time
 */
@Interceptor
public class ResponseTimeInterceptor implements IClientInterceptor {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ResponseTimeInterceptor.class);

	/**
	 * Variable to store the sum of HTTP response time in milliseconds
	 */
	private static long sum = 0;

	/**
	 * Variable to store the number of executed requests
	 */
	private static int requests = 0;

	/**
	 * Variable to store the maximum number of request execution upon which average
	 * response time should be logged
	 */
	private int numberOfRequests;

	public ResponseTimeInterceptor(int numberOfRequests) {
		super();
		this.numberOfRequests = numberOfRequests;
	}

	@Override
	@Hook(Pointcut.CLIENT_REQUEST)
	public void interceptRequest(IHttpRequest theRequest) {
		// do nothing
	}

	/**
	 * Method to intercept the HTTP response before it is processed. <br><br> 
	 * 
	 * <b>requests</b> will maintain the running sum of number of HTTP calls. When it is equal to
	 * numberOfRequests then reset it. <br><br>
	 * 
	 * <b>sum</b> will maintain the running sum of HTTP response time. Use it to log the average response time of
	 * numberOfRequests requests
	 */
	@Override
	@Hook(Pointcut.CLIENT_RESPONSE)
	public void interceptResponse(IHttpResponse theResponse) throws IOException {
		requests++;
		StopWatch watch = theResponse.getRequestStopWatch();
		sum += watch.getMillis();
		if (numberOfRequests == requests) {
			logger.info("Average Response Time of last {} calls is {} ms.", numberOfRequests, sum / numberOfRequests);
			sum = 0;
			requests = 0;
		}
	}

}
