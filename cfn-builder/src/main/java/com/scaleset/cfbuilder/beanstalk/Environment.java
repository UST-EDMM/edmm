package com.scaleset.cfbuilder.beanstalk;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Taggable;

/**
 * Constructs or updates a {@code Environment}, an ElasticBeanstalk environment.
 * <br>
 * Type: {@code AWS::ElasticBeanstalk::Environment}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-beanstalk-environment.html">Documentation
 * Reference</a>
 */
@Type("AWS::ElasticBeanstalk::Environment")
public interface Environment extends Taggable {

    Environment applicationName(Object applicationName);

    Environment cNAMEPrefix(String cNAMEPrefix);

    Environment description(String description);

    Environment environmentName(String environmentName);

    Environment optionSettings(OptionSetting... optionSettings);

    Environment platformArn(Object platformArn);

    Environment solutionStackName(Object solutionStackName);

    Environment templateName(Object templateName);

    Environment tier(EnvironmentTier tier);

    Environment versionLabel(Object versionLabel);
}
