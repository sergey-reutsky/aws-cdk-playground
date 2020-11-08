package com.playground.app;

import software.amazon.awscdk.core.App;

public class Application
{

	private static final String REPOSITORY_NAME = "aws-cdk-playground";

	public static void main(final String[] argv)
	{
		final App app = new App();
		final LambdaStack lambdaStack = new LambdaStack(app, "LambdaStack");
		new PipelineStack(app, "PipelineStack",
				lambdaStack.getLambdaCode(), REPOSITORY_NAME);

		app.synth();
	}
}