package com.scaleset.cfbuilder.elasticloadbalancing;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Resource;

/**
 * Constructs a {@code LoadBalancer} to create a LoadBalancer.
 * <br>
 * Type: {@code AWS::ElasticLoadBalancing::LoadBalancer}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-elb.html">Documentation
 * Reference</a>
 */
@Type("AWS::ElasticLoadBalancing::LoadBalancer")
public interface LoadBalancer extends Resource {

    LoadBalancer accessLoggingPolicy(Object value);

    LoadBalancer appCookieStickinessPolicy(Object... values);

    LoadBalancer availabilityZones(Object... values);
}
