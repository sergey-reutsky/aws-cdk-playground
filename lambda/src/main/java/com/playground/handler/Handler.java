package com.playground.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Object, String>
{

	@Override
	public String handleRequest(Object event, Context context)
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