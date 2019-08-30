package io.github.edmm.plugins.azure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.github.edmm.core.plugin.JsonHelper;
import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Compute;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class AzureVisitorTest {
    private static final Logger logger = LoggerFactory.getLogger(AzureVisitorTest.class);

    @Test
    public void testVisitingComputeNode() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/one_compute/definitions.yml");
        DeploymentModel model = DeploymentModel.of(resource.getFile());
        AzureVisitor visitor = new AzureVisitor(model.getTopology());
        visitor.visit((Compute) model.getComponent("pet_clinic_ubuntu").get());
        String json = JsonHelper.serializeObj(visitor.getResultTemplate());
        logger.debug(json);
        ClassPathResource expectedResource = new ClassPathResource("azure/oneCompute.json");
        String expected = FileUtils.readFileToString(expectedResource.getFile(), StandardCharsets.UTF_8);
        Assert.assertEquals(expected.trim(), json.trim());
    }
}