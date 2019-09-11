package com.scaleset.cfbuilder.ec2.instance.ssmassociation;

import java.util.ArrayList;
import java.util.List;

import com.scaleset.cfbuilder.ec2.instance.SSMAssociation;

/**
 * Constructs an {@code AssociationParameter} to specify input parameter values for an Amazon EC2 Systems Manager (SSM)
 * document.
 * <br>
 * Property of EC2 Instance {@link SSMAssociation}.
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-instance-ssmassociations-associationparameters.html">Documentation
 * Reference</a>
 */
public class AssociationParameter {
    private String key;
    private List<String> value;

    public AssociationParameter() {
        this.value = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AssociationParameter key(String key) {
        this.key = key;
        return this;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public AssociationParameter value(List<String> value) {
        this.value = value;
        return this;
    }

    public AssociationParameter addValue(String value) {
        this.value.add(value);
        return this;
    }
}
