package com.scaleset.cfbuilder.ec2.instance;

import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.instance.ec2blockdevicemapping.EC2EBSBlockDevice;

/**
 * Constructs an {@code EC2BlockDeviceMapping} to be embedded în an {@link Instance} resource.
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-blockdev-mapping.html">Documentation
 * Reference</a>
 */
public class EC2BlockDeviceMapping {
    private String deviceName;
    private EC2EBSBlockDevice ebs;
    private Boolean noDevice;
    private String virtualName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public EC2BlockDeviceMapping deviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public EC2EBSBlockDevice getEbs() {
        return ebs;
    }

    public void setEbs(EC2EBSBlockDevice ebs) {
        this.ebs = ebs;
    }

    public EC2BlockDeviceMapping ebs(EC2EBSBlockDevice ebs) {
        this.ebs = ebs;
        return this;
    }

    public Boolean isNoDevice() {
        return noDevice;
    }

    public void setNoDevice(Boolean noDevice) {
        this.noDevice = noDevice;
    }

    public EC2BlockDeviceMapping noDevice(Boolean noDevice) {
        this.noDevice = noDevice;
        return this;
    }

    public String getVirtualName() {
        return virtualName;
    }

    public void setVirtualName(String virtualName) {
        this.virtualName = virtualName;
    }

    public EC2BlockDeviceMapping virtualName(String virtualName) {
        this.virtualName = virtualName;
        return this;
    }
}
