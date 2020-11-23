package com.playground.app;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.codebuild.BuildEnvironment;
import software.amazon.awscdk.services.codebuild.BuildSpec;
import software.amazon.awscdk.services.codebuild.ComputeType;
import software.amazon.awscdk.services.codebuild.LinuxBuildImage;
import software.amazon.awscdk.services.codebuild.PipelineProject;
import software.amazon.awscdk.services.codepipeline.Artifact;
import software.amazon.awscdk.services.codepipeline.Pipeline;
import software.amazon.awscdk.services.codepipeline.StageProps;
import software.amazon.awscdk.services.codepipeline.actions.CloudFormationCreateUpdateStackAction;
import software.amazon.awscdk.services.codepipeline.actions.CodeBuildAction;
import software.amazon.awscdk.services.codepipeline.actions.GitHubSourceAction;
import software.amazon.awscdk.services.lambda.CfnParametersCode;
import software.amazon.awscdk.services.secretsmanager.ISecret;
import software.amazon.awscdk.services.secretsmanager.Secret;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PipelineStack extends Stack
{
	// alternate constructor for calls without props.
	// lambdaCode, repoName and repoOwner are required.
	public PipelineStack(final App scope, final String id,
	                     final CfnParametersCode lambdaCode, final String repoName, final String repoOwner)
	{
		this(scope, id, null, lambdaCode, repoName, repoOwner);
	}

	@SuppressWarnings("serial")
	public PipelineStack(final App scope, final String id, final StackProps props,
	                     final CfnParametersCode lambdaCode, final String repoName, final String repoOwner)
	{
		super(scope, id, props);

		final ISecret secret = Secret.fromSecretNameV2(this, "Secret", "repoOauthToken");

		final PipelineProject cdkBuild = PipelineProject.Builder.create(this, "CDKBuild")
				.buildSpec(BuildSpec.fromObject(new HashMap<String, Object>()
				{{
					put("version", "0.2");
					put("phases", new HashMap<String, Object>()
					{{
						put("install", new HashMap<String, String>()
						{{
							put("commands", "npm install aws-cdk");
						}});
						put("build", new HashMap<String, Object>()
						{{
							put("commands", Arrays.asList("mvn compile -q -DskipTests",
									"npx cdk synth -o dist"));
						}});
					}});
					put("artifacts", new HashMap<String, Object>()
					{{
						put("base-directory", "dist");
						put("files", Arrays.asList(
								"ApiEventHandlerStack.template.json",
								"ApplicationInfrastructureStack.template.json"));
					}});
				}}))
				.environment(BuildEnvironment.builder()
						.computeType(ComputeType.SMALL)
						.buildImage(LinuxBuildImage.STANDARD_2_0)
						.build())
				.build();

		final PipelineProject lambdaBuild = PipelineProject.Builder.create(this, "ApiEventHandlerBuild")
				.buildSpec(BuildSpec.fromObject(new HashMap<String, Object>()
				{{
					put("version", "0.2");
					put("phases", new HashMap<String, Object>()
					{{
						put("install", new HashMap<String, List<String>>()
						{{
							put("commands", Arrays.asList(
									"cd ApiEventHandler",
									"apt-get update -y",
									"apt-get install -y maven"));
						}});
						put("build", new HashMap<String, List<String>>()
						{{
							put("commands", Collections.singletonList("mvn clean package"));
						}});
					}});
					put("artifacts", new HashMap<String, Object>()
					{{
						put("base-directory", "ApiEventHandler/target/assembly");
						put("files", Collections.singletonList("**/*"));
					}});
				}}))
				.environment(BuildEnvironment.builder().buildImage(
						LinuxBuildImage.STANDARD_2_0).build())
				.build();

		final Artifact sourceOutput = new Artifact();
		final Artifact cdkBuildOutput = new Artifact("CdkBuildOutput");
		final Artifact apiEventHandlerBuildOutput = new Artifact("ApiEventHandlerBuildOutput");

		Pipeline.Builder.create(this, "Pipeline")
				.stages(Arrays.asList(
						StageProps.builder()
								.stageName("Source")
								.actions(Collections.singletonList(
										GitHubSourceAction.Builder.create()
												.actionName("Source")
												.branch("master")
												.repo(repoName)
												.owner(repoOwner)
												.oauthToken(secret.getSecretValue())
												.output(sourceOutput)
												.build()))
								.build(),
						StageProps.builder()
								.stageName("Build")
								.actions(Arrays.asList(
										CodeBuildAction.Builder.create()
												.actionName("ApiEventHandler_Build")
												.project(lambdaBuild)
												.input(sourceOutput)
												.outputs(Collections.singletonList(apiEventHandlerBuildOutput)).build(),
										CodeBuildAction.Builder.create()
												.actionName("CDK_Build")
												.project(cdkBuild)
												.input(sourceOutput)
												.outputs(Collections.singletonList(cdkBuildOutput))
												.build()))
								.build(),
						StageProps.builder()
								.stageName("Deploy")
								.actions(Arrays.asList(
										CloudFormationCreateUpdateStackAction.Builder.create()
												.actionName("ApiEventHandler_CFN_Deploy")
												.runOrder(1)
												.templatePath(cdkBuildOutput.atPath("ApiEventHandlerStack.template.json"))
												.adminPermissions(true)
												.parameterOverrides(lambdaCode.assign(apiEventHandlerBuildOutput.getS3Location()))
												.extraInputs(Collections.singletonList(apiEventHandlerBuildOutput))
												.stackName("ApiEventHandlerDeploymentStack")
												.build(),
										CloudFormationCreateUpdateStackAction.Builder.create()
												.actionName("AppInfrastructure_CFN_Deploy")
												.runOrder(2)
												.templatePath(cdkBuildOutput.atPath("ApplicationInfrastructureStack.template.json"))
												.adminPermissions(true)
												.stackName("AppInfrastructureDeploymentStack")
												.build()))
								.build()))
				.build();
	}
}