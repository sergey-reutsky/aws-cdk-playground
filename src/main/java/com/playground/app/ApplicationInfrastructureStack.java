package com.playground.app;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.Function;

public class ApplicationInfrastructureStack extends Stack
{

	public ApplicationInfrastructureStack(final App scope, final String id, Function apiEventHandler)
	{
		this(scope, id, apiEventHandler, null);
	}

	public ApplicationInfrastructureStack(final App scope, final String id, Function apiEventHandler, final StackProps props)
	{
		super(scope, id, props);

		final RestApi api = RestApi.Builder.create(this, "SimpleServiceAPI")
				.restApiName("Simple Service")
				.description("Simple service to test lambda integration")
				.build();

		final LambdaIntegration lambdaIntegration = LambdaIntegration.Builder.create(apiEventHandler)
				.build();

		api.getRoot().addMethod("POST", lambdaIntegration);
	}
}