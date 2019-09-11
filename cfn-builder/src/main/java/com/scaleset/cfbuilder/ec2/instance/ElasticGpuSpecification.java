package com.scaleset.cfbuilder.ec2.instance;

import com.scaleset.cfbuilder.ec2.Instance;

/**
 * Constructs a {@code ElasticGpuSpecification} to accelerate the graphics performance of your applications.
 * <br>
 * Property of the EC2 {@link Instance} resource.
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-instance-elasticgpuspecification.html">Documentation
 * Reference</a>
 */
public class ElasticGpuSpecification {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ElasticGpuSpecification type(String type) {
        this.type = type;
        return this;
    }
}
