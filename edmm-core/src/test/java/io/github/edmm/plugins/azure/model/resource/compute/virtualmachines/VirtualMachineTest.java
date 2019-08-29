package io.github.edmm.plugins.azure.model.resource.compute.virtualmachines;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.github.edmm.core.plugin.JsonHelper;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class VirtualMachineTest {
    private static final Logger logger = LoggerFactory.getLogger(VirtualMachineTest.class);

    @Test
    public void testSerialization() throws IOException {
        VirtualMachine defaultObject = new VirtualMachine("vm1");
        String serializedObj = JsonHelper.serializeObj(defaultObject);
        logger.debug(serializedObj);
        ClassPathResource expectedResource = new ClassPathResource("azure/virtualMachine.json");
        String expected = FileUtils.readFileToString(expectedResource.getFile(), StandardCharsets.UTF_8);
        Assert.assertEquals(expected.trim(), serializedObj.trim());
    }
}