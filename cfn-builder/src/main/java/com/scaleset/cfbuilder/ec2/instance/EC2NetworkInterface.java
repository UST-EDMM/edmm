package com.scaleset.cfbuilder.ec2.instance;

import java.util.ArrayList;
import java.util.List;

import com.scaleset.cfbuilder.ec2.Instance;
import com.scaleset.cfbuilder.ec2.networkinterface.Ipv6Address;
import com.scaleset.cfbuilder.ec2.networkinterface.PrivateIpAddressSpecification;

/**
 * Constructs a {@code EC2NetworkInterface} to be attached to an EC2 {@link Instance}.
 *
 * @see <a href="https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-network-iface-embedded.html">Documentation
 * Reference</a>
 */
public class EC2NetworkInterface {
    private Boolean associatePublicIpAddress;
    private Boolean deleteOnTermination;
    private String description;
    private String deviceIndex;
    private List<Object> groupSet;
    private Object networkInterfaceId;
    private Integer ipv6AddressCount;
    private List<Ipv6Address> ipv6Addresses;
    private String privateIpAddress;
    private List<PrivateIpAddressSpecification> privateIpAddresses;
    private Integer secondaryPrivateIpAddressCount;
    private Object subnetId;

    public EC2NetworkInterface() {
        this.groupSet = new ArrayList<>();
        this.ipv6Addresses = new ArrayList<>();
        this.privateIpAddresses = new ArrayList<>();
    }

    public Boolean isAssociatePublicIpAddress() {
        return associatePublicIpAddress;
    }

    public void setAssociatePublicIpAddress(Boolean associatePublicIpAddress) {
        this.associatePublicIpAddress = associatePublicIpAddress;
    }

    public EC2NetworkInterface associatePublicIpAddress(Boolean associatePublicIpAddress) {
        this.associatePublicIpAddress = associatePublicIpAddress;
        return this;
    }

    public Boolean isDeleteOnTermination() {
        return deleteOnTermination;
    }

    public void setDeleteOnTermination(Boolean deleteOnTermination) {
        this.deleteOnTermination = deleteOnTermination;
    }

    public EC2NetworkInterface deleteOnTermination(Boolean deleteOnTermination) {
        this.deleteOnTermination = deleteOnTermination;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EC2NetworkInterface description(String description) {
        this.description = description;
        return this;
    }

    public String getDeviceIndex() {
        return deviceIndex;
    }

    public void setDeviceIndex(String deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    public EC2NetworkInterface deviceIndex(String deviceIndex) {
        this.deviceIndex = deviceIndex;
        return this;
    }

    public List<Object> getGroupSet() {
        return groupSet;
    }

    public void setGroupSet(List<Object> groupSet) {
        this.groupSet = groupSet;
    }

    public EC2NetworkInterface groupSet(List<Object> groupSet) {
        this.groupSet = groupSet;
        return this;
    }

    public EC2NetworkInterface addGroupSet(Object groupID) {
        this.groupSet.add(groupID);
        return this;
    }

    public Object getNetworkInterfaceId() {
        return networkInterfaceId;
    }

    public void setNetworkInterfaceId(Object networkInterfaceId) {
        this.networkInterfaceId = networkInterfaceId;
    }

    public EC2NetworkInterface networkInterfaceId(Object networkInterfaceId) {
        this.networkInterfaceId = networkInterfaceId;
        return this;
    }

    public Integer getIpv6AddressCount() {
        return ipv6AddressCount;
    }

    public void setIpv6AddressCount(Integer ipv6AddressCount) {
        this.ipv6AddressCount = ipv6AddressCount;
    }

    public EC2NetworkInterface ipv6AddressCount(Integer ipv6AddressCount) {
        this.ipv6AddressCount = ipv6AddressCount;
        return this;
    }

    public List<Ipv6Address> getIpv6Addresses() {
        return ipv6Addresses;
    }

    public void setIpv6Addresses(List<Ipv6Address> ipv6Addresses) {
        this.ipv6Addresses = ipv6Addresses;
    }

    public EC2NetworkInterface ipv6Addresses(List<Ipv6Address> ipv6Addresses) {
        this.ipv6Addresses = ipv6Addresses;
        return this;
    }

    public EC2NetworkInterface addIpv6Addresses(Ipv6Address ipv6Address) {
        this.ipv6Addresses.add(ipv6Address);
        return this;
    }

    public String getPrivateIpAddress() {
        return privateIpAddress;
    }

    public void setPrivateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
    }

    public EC2NetworkInterface privateIpAddress(String privateIpAddress) {
        this.privateIpAddress = privateIpAddress;
        return this;
    }

    public List<PrivateIpAddressSpecification> getPrivateIpAddresses() {
        return privateIpAddresses;
    }

    public void setPrivateIpAddresses(List<PrivateIpAddressSpecification> privateIpAddresses) {
        this.privateIpAddresses = privateIpAddresses;
    }

    public EC2NetworkInterface privateIpAddresses(List<PrivateIpAddressSpecification> privateIpAddresses) {
        this.privateIpAddresses = privateIpAddresses;
        return this;
    }

    public EC2NetworkInterface addPrivateIpAddresses(PrivateIpAddressSpecification privateIpAddressSpecification) {
        privateIpAddresses.add(privateIpAddressSpecification);
        return this;
    }

    public Integer getSecondaryPrivateIpAddressCount() {
        return secondaryPrivateIpAddressCount;
    }

    public void setSecondaryPrivateIpAddressCount(Integer secondaryPrivateIpAddressCount) {
        this.secondaryPrivateIpAddressCount = secondaryPrivateIpAddressCount;
    }

    public EC2NetworkInterface secondaryPrivateIpAddressCount(Integer secondaryPrivateIpAddressCount) {
        this.secondaryPrivateIpAddressCount = secondaryPrivateIpAddressCount;
        return this;
    }

    public Object getSubnetId() {
        return subnetId;
    }

    public EC2NetworkInterface subnetId(Object subnetId) {
        this.subnetId = subnetId;
        return this;
    }

    public void setSubnetId(Object subnetId) {
        this.subnetId = subnetId;
    }
}
