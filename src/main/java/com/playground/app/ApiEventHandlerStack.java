package com.playground.app;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Duration;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.codedeploy.LambdaDeploymentConfig;
import software.amazon.awscdk.services.codedeploy.LambdaDeploymentGroup;
import software.amazon.awscdk.services.lambda.Alias;
import software.amazon.awscdk.services.lambda.CfnParametersCode;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Version;

public class ApiEventHandlerStack extends Stack
{

	private final CfnParametersCode lambdaCode;
	private final Function apiEventHandler;

	public ApiEventHandlerStack(final App scope, final String id)
	{
		this(scope, id, null);
	}

	public ApiEventHandlerStack(final App scope, final String id, final StackProps props)
	{
		super(scope, id, props);

		lambdaCode = CfnParametersCode.fromCfnParameters();

		apiEventHandler = Function.Builder.create(this, "ApiEventHandler")
				.code(lambdaCode)
				.functionName("ApiEventHandler")
				.handler("com.playground.handler.Handler::handleRequest")
				.timeout(Duration.seconds(10))
				.runtime(Runtime.JAVA_8_CORRETTO).build();

		final Version version = apiEventHandler.getCurrentVersion();
		final Alias alias = Alias.Builder.create(this, "ApiEventHandlerAlias")
				.aliasName("ApiEventHandlerAlias")
				.version(version).build();

		LambdaDeploymentGroup.Builder.create(this, "ApiEventHandlerDeploymentGroup")
				.alias(alias)
				.deploymentConfig(LambdaDeploymentConfig.ALL_AT_ONCE).build();
	}

	public CfnParametersCode getLambdaCode()
	{
		return lambdaCode;
	}

	public Function getApiEventHandler()
	{
		return apiEventHandler;
	}
}
