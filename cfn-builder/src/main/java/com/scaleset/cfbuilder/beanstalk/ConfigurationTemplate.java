package com.scaleset.cfbuilder.beanstalk;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code ConfigurationTemplate} for an ElasticBeanstalk {@link Application}.
 * <br>
 * Type: {@code AWS::ElasticBeanstalk::ConfigurationTemplate}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-beanstalk-configurationtemplate.html">Documentation
 * Reference</a>
 */
@Type("AWS::ElasticBeanstalk::ConfigurationTemplate")
public interface ConfigurationTemplate extends Resource {

    ConfigurationTemplate applicationName(Object applicationName);

    ConfigurationTemplate description(String description);

    ConfigurationTemplate environmentId(Object environmentId);

    ConfigurationTemplate optionSettings(OptionSetting... optionSettings);

    ConfigurationTemplate platformArn(Object platformArn);

    ConfigurationTemplate solutionStackName(Object solutionStackName);

    ConfigurationTemplate sourceConfiguration(SourceConfiguration sourceConfiguration);
}
