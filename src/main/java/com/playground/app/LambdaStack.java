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

public class LambdaStack extends Stack
{

	// private attribute to hold our Lambda's code, with public getters
	private final CfnParametersCode lambdaCode;

	// Constructor without props argument
	public LambdaStack(final App scope, final String id)
	{
		this(scope, id, null);
	}

	public LambdaStack(final App scope, final String id, final StackProps props)
	{
		super(scope, id, props);

		lambdaCode = CfnParametersCode.fromCfnParameters();

		Function func = Function.Builder.create(this, "Lambda")
				.code(lambdaCode)
				.functionName("SimpleLambda")
				.handler("com.playground.lambda.Lambda::handleRequest")
				.timeout(Duration.seconds(10))
				.runtime(Runtime.JAVA_8_CORRETTO).build();

		Version version = func.getCurrentVersion();
		Alias alias = Alias.Builder.create(this, "LambdaAlias")
				.aliasName("LambdaAlias")
				.version(version).build();

		LambdaDeploymentGroup.Builder.create(this, "DeploymentGroup")
				.alias(alias)
				.deploymentConfig(LambdaDeploymentConfig.ALL_AT_ONCE).build();
	}

	public CfnParametersCode getLambdaCode()
	{
		return lambdaCode;
	}
}