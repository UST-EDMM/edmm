package com.scaleset.cfbuilder.iam;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code User} to create a user in your CloudFormation template.
 * <br>
 * Type: {@code AWS::IAM::User}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-iam-user.html">Documentation
 * Reference</a>
 */
@Type("AWS::IAM::User")
public interface User extends Resource {

    User path(Object value);

    User groups(Object... value);

    User loginProfile(Object profile);

    User policies(Object... policies);
}
