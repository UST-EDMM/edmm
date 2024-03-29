package com.scaleset.cfbuilder.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaleset.cfbuilder.ec2.Instance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResourceProxyTest {

    @Test
    public void testRef() throws JsonProcessingException {
        ResourceInvocationHandler<Instance> proxy = new ResourceInvocationHandler<>(Instance.class, "id");

        Instance r = proxy.proxy();
        assertNotNull(r);

        r.name("name");

        ObjectMapper mapper = new ObjectMapper().registerModule(new CloudFormationJsonModule());
        String json = mapper.writeValueAsString(r);
        assertNotNull(json);
    }
}
