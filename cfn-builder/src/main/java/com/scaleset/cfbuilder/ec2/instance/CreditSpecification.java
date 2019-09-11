package com.scaleset.cfbuilder.ec2.instance;

import com.scaleset.cfbuilder.ec2.Instance;

/**
 * Constructs a {@code CreditSpecification} to specify the credit option for CPU usage of a T2 instance.
 * <br>
 * Property of the EC2 {@link Instance} resource.
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-instance-creditspecification.html">Documentation
 * Reference</a>
 */
public class CreditSpecification {
    private String cPUCredits;

    public String getcPUCredits() {
        return cPUCredits;
    }

    public void setcPUCredits(String cPUCredits) {
        this.cPUCredits = cPUCredits;
    }

    public CreditSpecification cPUCredits(String cPUCredits) {
        this.cPUCredits = cPUCredits;
        return this;
    }
}
