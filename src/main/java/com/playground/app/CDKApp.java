package com.playground.app;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import software.amazon.awscdk.core.App;

public class CDKApp
{

	private static final String REPOSITORY_NAME_PROPERTY = "repository.name";
	private static final String REPOSITORY_OWNER_PROPERTY = "repository.owner";

	public static void main(final String[] argv) throws ConfigurationException
	{
		final PropertiesConfiguration config = new PropertiesConfiguration("config.properties");
		final String repositoryName = config.getString(REPOSITORY_NAME_PROPERTY);
		final String repositoryOwner = config.getString(REPOSITORY_OWNER_PROPERTY);

		final App app = new App();
		final ApiEventHandlerStack apiEventHandlerStack =
				new ApiEventHandlerStack(app, "ApiEventHandlerStack");

		new ApplicationInfrastructureStack(app, "ApplicationInfrastructureStack",
						apiEventHandlerStack.getApiEventHandler());

		new PipelineStack(app, "PipelineStack",
				apiEventHandlerStack.getLambdaCode(), repositoryName, repositoryOwner);

		app.synth();
	}
}