package com.scaleset.cfbuilder.beanstalk;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code ApplicationVersion}, an iteration of deployable code, for an ElasticBeanstalk {@link
 * Application}.
 * <br>
 * Type: {@code AWS::ElasticBeanstalk::ApplicationVersion}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-beanstalk-version.html">Documentation
 * Reference</a>
 */
@Type("AWS::ElasticBeanstalk::ApplicationVersion")
public interface ApplicationVersion extends Resource {

    ApplicationVersion applicationName(Object applicationName);

    ApplicationVersion description(String description);

    ApplicationVersion sourceBundle(SourceBundle sourceBundle);
}
