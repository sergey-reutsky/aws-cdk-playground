package com.playground.app;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.Function;

import java.util.HashMap;

public class ApiGatewayStack extends Stack
{

	public ApiGatewayStack(final App scope, final String id, Function handler)
	{
		this(scope, id, null, handler);
	}

	public ApiGatewayStack(final App scope, final String id, final StackProps props, Function handler)
	{
		super(scope, id, props);

		final RestApi api = RestApi.Builder.create(this, "REST-API")
				.restApiName("Simple Service").description("Simple service to test lambda integration")
				.build();

		final LambdaIntegration lambdaIntegration = LambdaIntegration.Builder.create(handler)
				.build();

		api.getRoot().addMethod("POST", lambdaIntegration);
	}
}
