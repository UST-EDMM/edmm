package com.scaleset.cfbuilder.rds;

import com.scaleset.cfbuilder.annotations.Type;
import com.scaleset.cfbuilder.core.Taggable;

/**
 * Constructs a {@code DBInstance} to create an Amazon Relational Database Service (Amazon RDS) DB instance.
 * <br>
 * Type: {@code AWS::RDS::DBInstance}
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-rds-database-instance.html">Documentation
 * Reference</a>
 */
@Type("AWS::RDS::DBInstance")
public interface DBInstance extends Taggable {

    DBInstance engine(String engine);

    DBInstance dBName(String dBName);

    DBInstance masterUsername(String masterUsername);

    DBInstance masterUserPassword(String masterUserPassword);

    DBInstance dBInstanceClass(String dBInstanceClass);

    DBInstance allocatedStorage(Integer allocatedStorage);

    DBInstance storageType(String storageType);

    DBInstance vPCSecurityGroups(Object... vPCSecurityGroup);

    default DBInstance name(String name) {
        tag("Name", name);
        return this;
    }
}
