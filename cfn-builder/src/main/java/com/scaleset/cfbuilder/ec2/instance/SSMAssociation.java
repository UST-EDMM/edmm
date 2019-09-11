package com.scaleset.cfbuilder.ec2.instance;

import java.util.ArrayList;
import java.util.List;

import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.instance.ssmassociation.AssociationParameter;

/**
 * Constructs a {@code SSMAssociation} to specify a Amazon EC2 Systems Manager (SSM) document and parameter values.
 * <br>
 * Property of EC2 {@link Instance}.
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-instance-ssmassociations.html">Documentation
 * Reference</a>
 */
public class SSMAssociation {
    private List<AssociationParameter> associationParameters;
    private String documentName;

    public SSMAssociation() {
        this.associationParameters = new ArrayList<>();
    }

    public List<AssociationParameter> getAssociationParameters() {
        return associationParameters;
    }

    public void setAssociationParameters(List<AssociationParameter> associationParameters) {
        this.associationParameters = associationParameters;
    }

    public SSMAssociation associationParameters(List<AssociationParameter> associationParameters) {
        this.associationParameters = associationParameters;
        return this;
    }

    public SSMAssociation addAssociationParameters(AssociationParameter associationParameter) {
        this.associationParameters.add(associationParameter);
        return this;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public SSMAssociation documentName(String documentName) {
        this.documentName = documentName;
        return this;
    }
}
