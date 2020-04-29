package io.github.edmm.plugins.rules;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.util.StringInputStream;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.model.DeploymentModel;
import org.junit.Test;
import org.junit.Assert;
import org.yaml.snakeyaml.Yaml;

public class RulesTests {

    @Test
    public void testToTopology() {
        Map<String,Object> map = new HashMap<>();
        List<Map> list = new ArrayList<>();
        Map<String,Object> submap = new HashMap<>();
        submap.put("hosted_on", "compute");
        list.add(submap);
        map.put("type" , list );
        System.out.println(new Yaml().dumpAsMap(map));


    }

    @Test
    public void testEvaluate() {
        PaasDefaultRule paasDefaultRule = new PaasDefaultRule();
    }
}
