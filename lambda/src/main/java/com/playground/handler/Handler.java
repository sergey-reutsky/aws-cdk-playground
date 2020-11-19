package com.playground.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class Handler implements RequestHandler<Map<String,String>, String>
{

	final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public String handleRequest(Map<String,String> event, Context context)
	{
		final LambdaLogger logger = context.getLogger();

		// process event
		logger.log("EVENT: " + gson.toJson(event));
		logger.log("EVENT TYPE: " + event.getClass().toString());

		Map<String, Object> response = new HashMap<>();
		response.put("statusCode", "200");
		response.put("isBase64Encoded", false);

		return gson.toJson(response);
	}
}