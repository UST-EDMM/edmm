package com.scaleset.cfbuilder.beanstalk;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code Application}, an ElasticBeanstalk application.
 * <br>
 * Type: {@code AWS::ElasticBeanstalk::Application}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-beanstalk.html">Documentation
 * Reference</a>
 */
@Type("AWS::ElasticBeanstalk::Application")
public interface Application extends Resource {

    Application applicationName(String applicationName);

    Application description(String description);

    Application resourceLifecycleConfig(ApplicationResourceLifecycleConfig resourceLifecycleConfig);
}
