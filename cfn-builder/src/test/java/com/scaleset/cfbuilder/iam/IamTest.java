package com.scaleset.cfbuilder.iam;

import java.util.ArrayList;
import java.util.List;

import com.scaleset.cfbuilder.core.Module;
import com.scaleset.cfbuilder.core.Template;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class IamTest {
    @Test
    public void testIam() {
        Template testIamTemplate = new Template();
        new TestIamModule().id("").template(testIamTemplate).build();
        String testIamTemplateString = testIamTemplate.toString(true);

        assertNotNull(testIamTemplate);
        System.err.println(testIamTemplateString);
    }

    class TestIamModule extends Module {
        public void build() {
            List<String> resourceList = new ArrayList<>();
            resourceList.add("resourceVal");
            Principal principal = new Principal()
                    .principal("arnVal", resourceList);
            Statement statement = new Statement()
                    .addAction("actionVal")
                    .addResource("resourceVal")
                    .addNotAction("notActionVal")
                    .addNotResource("notResourceVal")
                    .principal(principal)
                    .notPrincipal(principal)
                    .effect("effectVal")
                    .sid("sidVal")
                    .conditon("conditionVal");
            PolicyDocument policyDocument = new PolicyDocument()
                    .version("versionVal")
                    .id("idVal")
                    .addStatement(statement);
            resource(Policy.class, "PolicyName")
                    .groups("groupVal1", "groupVal2")
                    .roles("roleVal1")
                    .users("userVal1")
                    .policyName("policyNameVal")
                    .policyDocument(policyDocument);
        }
    }
}
