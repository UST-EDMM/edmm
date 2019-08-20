package io.github.edmm.core.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class EntityGraphTest {

    private static EntityGraph graph;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/simple_props_only.yml");
        graph = new EntityGraph(resource.getInputStream());
    }

    @Test
    public void testBasicParsing() {
        Assert.assertEquals("edm_1_0", ((ScalarEntity) graph.getEntity(EntityGraph.ROOT.extend("version")).orElseThrow(IllegalStateException::new)).getValue());
    }
}
