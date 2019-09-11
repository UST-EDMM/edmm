package com.scaleset.cfbuilder.iam;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;
import com.scaleset.cfbuilder.ec2.Instance;

/**
 * Constructs an {@code InstanceProfile} to create an AWS Identity and Access Management (IAM) instance profile that can
 * be used with IAM {@link Role}s for EC2 {@link Instance}s.
 * <br>
 * Type: {@code AWS::IAM::InstanceProfile}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-iam-instanceprofile.html">Documentation
 * Reference</a>
 */
@Type("AWS::IAM::InstanceProfile")
public interface InstanceProfile extends Resource {

    InstanceProfile path(Object value);

    InstanceProfile roles(Object... values);

    InstanceProfile instanceProfileName(Object value);
}
