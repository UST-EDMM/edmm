package io.github.edmm.core.parser;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityGraphTest {

    @Test
    public void testBasicParsing() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/properties.yml");
        EntityGraph graph = new EntityGraph(resource.getInputStream());
        assertEquals("edm_1_0", ((ScalarEntity) graph.getEntity(EntityGraph.ROOT.extend("version"))
            .orElseThrow(IllegalStateException::new)).getValue());
    }

    @Test
    public void testParticipantParsingAndYamlGeneration() throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/unit-tests/participants.yml");
        ClassPathResource expectation = new ClassPathResource("templates/unit-tests/participants_expectation.yml");
        EntityGraph graph = new EntityGraph(resource.getInputStream());

        StringWriter writer = new StringWriter();
        graph.generateYamlOutput(writer);

        // Use JSONAssert to compare the YAML files
        ObjectMapper om = new ObjectMapper();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        JsonNode actual = objectMapper.readTree(writer.toString());
        JsonNode expected = objectMapper.readTree(IOUtils.toString(expectation.getInputStream(), StandardCharsets.UTF_8));
        JSONAssert.assertEquals(om.writeValueAsString(actual), om.writeValueAsString(expected), false);
    }
}
