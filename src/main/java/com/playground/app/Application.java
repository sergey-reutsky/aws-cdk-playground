package com.playground.app;

import software.amazon.awscdk.core.App;

public class Application
{

	private static final String REPOSITORY_NAME = "aws-cdk-playground";
	private static final String REPOSITORY_OWNER = "sergey-reutsky";

	public static void main(final String[] argv)
	{
		final App app = new App();
		final LambdaStack lambdaStack = new LambdaStack(app, "LambdaStack");
		final ApiGatewayStack apiGatewayStack = new ApiGatewayStack(app, "ApiGatewayStack",
				lambdaStack.getHandlerLambda());
		new PipelineStack(app, "PipelineStack",
				lambdaStack.getLambdaCode(), REPOSITORY_NAME, REPOSITORY_OWNER);

		app.synth();
	}
}