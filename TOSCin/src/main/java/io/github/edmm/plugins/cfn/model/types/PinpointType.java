package io.github.edmm.plugins.cfn.model.types;

import io.github.edmm.model.edimm.ComponentType;

public enum PinpointType {
    ADMChannel(ComponentType.Software_Component),
    APNSChannel(ComponentType.Software_Component),
    APNSSandboxChannel(ComponentType.Software_Component),
    APNSVoipChannel(ComponentType.Software_Component),
    APNSVoipSandboxChannel(ComponentType.Software_Component),
    App(ComponentType.SaaS),
    ApplicationSettings(ComponentType.Software_Component),
    BaiduChannel(ComponentType.Software_Component),
    Campaign(ComponentType.Software_Component),
    EmailChannel(ComponentType.Software_Component),
    EmailTemplate(ComponentType.Software_Component),
    EventStream(ComponentType.Software_Component),
    GCMChannel(ComponentType.Software_Component),
    PushTemplate(ComponentType.Software_Component),
    Segment(ComponentType.Software_Component),
    SMSChannel(ComponentType.Software_Component),
    SmsTemplate(ComponentType.Software_Component),
    VoiceChannel(ComponentType.Software_Component);

    ComponentType componentType;

    PinpointType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType toComponentType() {
        return this.componentType;
    }
}
