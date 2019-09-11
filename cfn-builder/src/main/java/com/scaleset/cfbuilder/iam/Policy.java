package com.scaleset.cfbuilder.iam;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code Policy} to associate an IAM policy with IAM users, roles, or groups.
 * <br>
 * Type: {@code AWS::IAM::Policy}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-iam-policy.html">Documentation
 * Reference</a>
 */
@Type("AWS::IAM::Policy")
public interface Policy extends Resource {

    Policy groups(Object... groups);

    Policy policyDocument(PolicyDocument value);

    Policy policyName(Object value);

    Policy roles(Object... roles);

    Policy users(Object... users);
}
