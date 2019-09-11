package com.scaleset.cfbuilder.iam;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code Role} to enable applications running on an EC2 instance to securely access AWS resources.
 * <br>
 * Type: {@code AWS::IAM::Role}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-iam-role.html">Documentation
 * Reference</a>
 */
@Type("AWS::IAM::Role")
public interface Role extends Resource {

    Role path(Object value);

    Role assumeRolePolicyDocument(Object value);

    Role policies(Object... policies);
}
