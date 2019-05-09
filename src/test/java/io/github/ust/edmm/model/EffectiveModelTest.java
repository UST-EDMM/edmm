package io.github.ust.edmm.model;

import io.github.ust.edmm.model.component.Compute;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class EffectiveModelTest {

    @Test
    public void testBasics() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/simple.yml");
        EffectiveModel model = EffectiveModel.of(resource.getFile());

        Compute ubuntu = (Compute) model.getComponents().get("ubuntu");

        Assert.assertEquals("Ubuntu node", ubuntu.getDescription().orElse(null));
        Assert.assertEquals(1, ubuntu.getMetadata().size());

        Assert.assertEquals(3, ubuntu.getProperties().size());
        Assert.assertEquals("22", ubuntu.getProperties().get("port").getValue());
        Assert.assertEquals("test", ubuntu.getProperties().get("os_family").getValue());
        Assert.assertEquals("test", ubuntu.getOsFamily().orElse(null));
        Assert.assertNull(ubuntu.getProperties().get("machine_image").getValue());
        Assert.assertNull(ubuntu.getMachineImage().orElse(null));




        // Assert.assertNotNull(ubuntu.getOsFamily().orElse(null));
        // Assert.assertEquals(1, ubuntu.getArtifacts().size());

        System.out.println(model);
    }
}
