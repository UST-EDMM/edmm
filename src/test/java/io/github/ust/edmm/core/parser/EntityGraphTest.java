package io.github.ust.edmm.core.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static io.github.ust.edmm.core.parser.EntityGraph.ROOT;

public class EntityGraphTest {

    private static EntityGraph graph;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/simple.yml");
        graph = new EntityGraph(resource.getInputStream());
    }

    @Test
    public void testBasicParsing() {
        Assert.assertEquals("edm_1_0", ((ScalarEntity) graph.getEntity(ROOT.extend("version")).orElseThrow(IllegalStateException::new)).getValue());
    }
}
