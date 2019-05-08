package io.github.ust.edmm.core.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class EntityGraphTest {

    private static EntityGraph graph;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/simple.yml");
        graph = new EntityGraph(resource.getInputStream());
    }

    @Test
    public void testContentOfObject() {
        Assert.assertEquals("edm_1_0", ((ScalarEntity) graph.getEntity(EntityGraph.ROOT.extend("version")).get()).getValue());
    }
}