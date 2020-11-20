package com.playground.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, Map<String, Object>>
{

	final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> event, Context context)
	{
		final LambdaLogger logger = context.getLogger();

		// process event
		logger.log("EVENT: " + gson.toJson(event));
		logger.log("EVENT TYPE: " + event.getClass().toString());

		final JsonObject headers = new JsonObject();
		headers.addProperty("Content-Type", "application/json");

		final Map<String, Object> response = new HashMap<>();
		response.put("statusCode", 200);
		response.put("isBase64Encoded", false);
		response.put("headers", gson.toJson(headers));
		response.put("body", "Success");

		return response;
	}
}