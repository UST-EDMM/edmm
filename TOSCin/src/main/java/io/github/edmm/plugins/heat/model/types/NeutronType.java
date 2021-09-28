package io.github.edmm.plugins.heat.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum NeutronType {
    AddressScope(ComponentType.Software_Component),
    ExtraRouteSet(ComponentType.Software_Component),
    Firewall(ComponentType.Software_Component),
    FirewallPolicy(ComponentType.Software_Component),
    FirewallRule(ComponentType.Software_Component),
    FloatingIP(ComponentType.Software_Component),
    FloatingIPAssociation(ComponentType.Software_Component),
    IKEPolicy(ComponentType.Software_Component),
    IPsecPolicy(ComponentType.Software_Component),
    IPsecSiteConnection(ComponentType.Software_Component),
    L2Gateway(ComponentType.Software_Component),
    L2GatewayConnection(ComponentType.Software_Component),
    MeteringLabel(ComponentType.Software_Component),
    Net(ComponentType.Software_Component),
    NetworkGateway(ComponentType.Software_Component),
    Port(ComponentType.Software_Component),
    ProviderNet(ComponentType.Software_Component),
    QoSBandwidthLimitRule(ComponentType.Software_Component),
    QoSDscpMarkingRule(ComponentType.Software_Component),
    QoSMinimumBandwidthRule(ComponentType.Software_Component),
    QoSPolicy(ComponentType.Software_Component),
    Quota(ComponentType.Software_Component),
    RBACPolicy(ComponentType.Software_Component),
    Router(ComponentType.Software_Component),
    RouterInterface(ComponentType.Software_Component),
    SecurityGroup(ComponentType.Software_Component),
    SecurityGroupRule(ComponentType.Software_Component),
    Segment(ComponentType.Software_Component),
    Subnet(ComponentType.Software_Component),
    SubnetPool(ComponentType.Software_Component),
    Trunk(ComponentType.Software_Component),
    VPNService(ComponentType.Software_Component),
    ;

    private ComponentType componentType;

    NeutronType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
