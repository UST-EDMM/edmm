package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum EC2Type {
    CapacityReservation(ComponentType.Software_Component),
    ClientVpnAuthorizationRule(ComponentType.Software_Component),
    ClientVpnEndpoint(ComponentType.Software_Component),
    ClientVpnRoute(ComponentType.Software_Component),
    ClientVpnTargetNetworkAssociation(ComponentType.Software_Component),
    CustomerGateway(ComponentType.Software_Component),
    DHCPOptions(ComponentType.Software_Component),
    EC2Fleet(ComponentType.Compute),
    EgressOnlyInternetGateway(ComponentType.Software_Component),
    EIP(ComponentType.Software_Component),
    EIPAssociation(ComponentType.Software_Component),
    FlowLog(ComponentType.Software_Component),
    GatewayRouteTableAssociation(ComponentType.Software_Component),
    Host(ComponentType.Compute),
    Instance(ComponentType.Compute),
    InternetGateway(ComponentType.Software_Component),
    LaunchTemplate(ComponentType.Software_Component),
    LocalGatewayRoute(ComponentType.Software_Component),
    LocalGatewayRouteTableVPCAssociation(ComponentType.Software_Component),
    NatGateway(ComponentType.Software_Component),
    NetworkAcl(ComponentType.Software_Component),
    NetworkAclEntry(ComponentType.Software_Component),
    NetworkInterface(ComponentType.Software_Component),
    NetworkInterfaceAttachment(ComponentType.Software_Component),
    NetworkInterfacePermission(ComponentType.Software_Component),
    PlacementGroup(ComponentType.Software_Component),
    Route(ComponentType.Software_Component),
    RouteTable(ComponentType.Software_Component),
    SecurityGroup(ComponentType.Software_Component),
    SecurityGroupEgress(ComponentType.Software_Component),
    SecurityGroupIngress(ComponentType.Software_Component),
    SpotFleet(ComponentType.Software_Component),
    Subnet(ComponentType.Software_Component),
    SubnetCidrBlock(ComponentType.Software_Component),
    SubnetNetworkAclAssociation(ComponentType.Software_Component),
    SubnetRouteTableAssociation(ComponentType.Software_Component),
    TrafficMirrorFilter(ComponentType.Software_Component),
    TrafficMirrorFilterRule(ComponentType.Software_Component),
    TrafficMirrorSession(ComponentType.Software_Component),
    TrafficMirrorTarget(ComponentType.Software_Component),
    TransitGateway(ComponentType.Software_Component),
    TransitGatewayAttachment(ComponentType.Software_Component),
    TransitGatewayRoute(ComponentType.Software_Component),
    TransitGatewayRouteTable(ComponentType.Software_Component),
    TransitGatewayRouteTableAssociation(ComponentType.Software_Component),
    TransitGatewayRouteTablePropagation(ComponentType.Software_Component),
    Volume(ComponentType.Software_Component),
    VolumeAttachment(ComponentType.Software_Component),
    VPC(ComponentType.Software_Component),
    VPCCidrBlock(ComponentType.Software_Component),
    VPCDHCPOptionsAssociation(ComponentType.Software_Component),
    VPCEndpoint(ComponentType.Software_Component),
    VPCEndpointConnectionNotification(ComponentType.Software_Component),
    VPCEndpointService(ComponentType.Software_Component),
    VPCEndpointServicePermissions(ComponentType.Software_Component),
    VPCGatewayAttachment(ComponentType.Software_Component),
    VPCPeeringConnection(ComponentType.Software_Component),
    VPNConnection(ComponentType.Software_Component),
    VPNConnectionRoute(ComponentType.Software_Component),
    VPNGateway(ComponentType.Software_Component),
    VPNGatewayRoutePropagation(ComponentType.Software_Component);

    ComponentType componentType;

    EC2Type(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
