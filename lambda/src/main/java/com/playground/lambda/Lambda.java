package com.playground.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Lambda implements RequestHandler<String, String>
{

	@Override
	public String handleRequest(String event, Context context)
	{
		LambdaLogger logger = context.getLogger();
		String response = "200 OK";
		// log execution details
		logger.log("ENVIRONMENT VARIABLES: " + System.getenv());
		logger.log("CONTEXT: " + context);
		// process event
		logger.log("EVENT: " + event);
		logger.log("EVENT TYPE: " + event.getClass().toString());

		return response;
	}
}