package com.scaleset.cfbuilder.ec2;

import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Template;
import com.scaleset.cfbuilder.ec2.networkinterface.Ipv6Address;
import com.scaleset.cfbuilder.ec2.networkinterface.PrivateIpAddressSpecification;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class NetworkInterfaceTest {

    @Test
    public void testNetworkInterface() {
        Template testNetworkInterfaceTemplate = new Template();
        new TestNetworkInterfaceModule().id("").template(testNetworkInterfaceTemplate).build();
        String testNetworkInterfaceTemplateString = testNetworkInterfaceTemplate.toString(true);

        assertNotNull(testNetworkInterfaceTemplate);
        System.err.println(testNetworkInterfaceTemplateString);
    }

    class TestNetworkInterfaceModule extends Module {
        public void build() {
            Ipv6Address ipv6Address = new Ipv6Address()
                    .ipv6Address("ipv6AddressVal");
            PrivateIpAddressSpecification privateIpAddressSpecification = new PrivateIpAddressSpecification()
                    .privateIpAddress("privateIpAddressVal")
                    .primary(true);
            resource(NetworkInterface.class, "NetworkInterfaceName")
                    .sourceDestCheck(true)
                    .groupSet("groupSetVal")
                    .description("descriptionVal")
                    .subnetId("subnetIdVal")
                    .name("nameVal")
                    .privateIpAddress("privateIpAddressVal")
                    .secondaryPrivateIpAddressCount(1)
                    .ipv6AddressCount(1)
                    .ipv6Addresses()
                    .privateIpAddresses();
        }
    }
}
