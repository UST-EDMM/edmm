package com.scaleset.cfbuilder.ec2.instance.ec2blockdevicemapping;

import com.scaleset.cfbuilder.ec2.instance.EC2BlockDeviceMapping;

/**
 * Constructs a {@code EC2EBSBlockDevice} to be embedded in a {@link EC2BlockDeviceMapping}.
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-blockdev-template.html">Documentation
 * Reference</a>
 */
public class EC2EBSBlockDevice {
    private Boolean deleteOnTermination;
    private Boolean encrypted;
    private Integer iops;
    private String snapshotId;
    private String volumeSize;
    private String volumeType;

    public Boolean isDeleteOnTermination() {
        return deleteOnTermination;
    }

    public void setDeleteOnTermination(Boolean deleteOnTermination) {
        this.deleteOnTermination = deleteOnTermination;
    }

    public EC2EBSBlockDevice deleteOnTermination(Boolean deleteOnTermination) {
        this.deleteOnTermination = deleteOnTermination;
        return this;
    }

    public Boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
    }

    public EC2EBSBlockDevice encrypted(Boolean encrypted) {
        this.encrypted = encrypted;
        return this;
    }

    public Integer getIops() {
        return iops;
    }

    public void setIops(Integer iops) {
        this.iops = iops;
    }

    public EC2EBSBlockDevice iops(Integer iops) {
        this.iops = iops;
        return this;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public EC2EBSBlockDevice snapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
        return this;
    }

    public String getVolumeSize() {
        return volumeSize;
    }

    public void setVolumeSize(String volumeSize) {
        this.volumeSize = volumeSize;
    }

    public EC2EBSBlockDevice volumeSize(String volumeSize) {
        this.volumeSize = volumeSize;
        return this;
    }

    public String getVolumeType() {
        return volumeType;
    }

    public void setVolumeType(String volumeType) {
        this.volumeType = volumeType;
    }

    public EC2EBSBlockDevice volumeType(String volumeType) {
        this.volumeType = volumeType;
        return this;
    }
}
